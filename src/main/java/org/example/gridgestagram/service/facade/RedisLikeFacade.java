package org.example.gridgestagram.service.facade;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.FeedLikeUserInfo;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLikeFacade {

    private static final String FEED_LIKE_COUNT_KEY = "feed:like:count:%d";
    private static final String FEED_LIKE_USERS_KEY = "feed:like:users:%d";
    private static final String USER_LIKED_FEEDS_KEY = "user:liked:feeds:%d";
    private static final String LIKE_SYNC_QUEUE_KEY = "like:sync:queue";

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeToggleResponse toggleLike(Long feedId, Long userId) {
        String likeCountKey = String.format(FEED_LIKE_COUNT_KEY, feedId);
        String likeUsersKey = String.format(FEED_LIKE_USERS_KEY, feedId);
        String userLikedKey = String.format(USER_LIKED_FEEDS_KEY, userId);

        Double score = stringRedisTemplate.opsForZSet().score(likeUsersKey, userId.toString());
        Boolean isLiked = (score != null);

        if (Boolean.TRUE.equals(isLiked)) {
            return removeLike(feedId, userId, likeCountKey, likeUsersKey, userLikedKey);
        } else {
            return addLike(feedId, userId, likeCountKey, likeUsersKey, userLikedKey);
        }
    }

    private LikeToggleResponse addLike(Long feedId, Long userId, String likeCountKey,
        String likeUsersKey, String userLikedKey) {
        long timestamp = System.currentTimeMillis();
        List<Object> results = stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForValue().increment(likeCountKey, 1);
                operations.opsForZSet().add(likeUsersKey, userId.toString(), timestamp);
                operations.opsForZSet().add(userLikedKey, feedId.toString(), timestamp);

                return operations.exec();
            }
        });

        addToSyncQueue(feedId, userId, "ADD");

        Integer likeCount = getLikeCount(feedId);

        log.info("좋아요 추가 완료 - 피드: {}, 사용자: {}, 현재 좋아요 수: {}", feedId, userId, likeCount);

        return LikeToggleResponse.builder()
            .liked(true)
            .likeCount(likeCount)
            .message("좋아요를 눌렀습니다.")
            .timestamp(LocalDateTime.now())
            .build();
    }

    private LikeToggleResponse removeLike(Long feedId, Long userId, String likeCountKey,
        String likeUsersKey, String userLikedKey) {

        // Redis Transaction 사용
        stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForValue().decrement(likeCountKey, 1);
                operations.opsForZSet().remove(likeUsersKey, userId.toString());
                operations.opsForZSet().remove(userLikedKey, feedId.toString());

                return operations.exec();
            }
        });

        addToSyncQueue(feedId, userId, "REMOVE");

        Integer likeCount = Math.max(0, getLikeCount(feedId));

        log.info("좋아요 취소 완료 - 피드: {}, 사용자: {}, 현재 좋아요 수: {}", feedId, userId, likeCount);

        return LikeToggleResponse.builder()
            .liked(false)
            .likeCount(likeCount)
            .message("좋아요를 취소했습니다.")
            .timestamp(LocalDateTime.now())
            .build();
    }

    public Integer getLikeCount(Long feedId) {
        String likeCountKey = String.format(FEED_LIKE_COUNT_KEY, feedId);
        String countStr = stringRedisTemplate.opsForValue().get(likeCountKey);
        return countStr != null ? Integer.parseInt(countStr) : 0;
    }

    public boolean isLikedByUser(Long feedId, Long userId) {
        String likeUsersKey = String.format(FEED_LIKE_USERS_KEY, feedId);
        return Boolean.TRUE.equals(
            stringRedisTemplate.opsForSet().isMember(likeUsersKey, userId.toString()));
    }

    public List<FeedLikeUserInfo> getFeedLikeUsers(Long feedId, int offset, int limit) {
        String likeUsersZSetKey = String.format(FEED_LIKE_USERS_KEY, feedId);

        Set<ZSetOperations.TypedTuple<String>> usersWithScores = stringRedisTemplate.opsForZSet()
            .reverseRangeWithScores(likeUsersZSetKey, offset, offset + limit - 1);

        if (usersWithScores == null || usersWithScores.isEmpty()) {
            return Collections.emptyList();
        }

        return usersWithScores.stream()
            .map(tuple -> FeedLikeUserInfo.builder()
                .userId(Long.parseLong(Objects.requireNonNull(tuple.getValue())))
                .likedTimestamp(Objects.requireNonNull(tuple.getScore()).longValue())
                .build())
            .toList();
    }

    private void addToSyncQueue(Long feedId, Long userId, String action) {
        Map<String, Object> syncData = Map.of(
            "feedId", feedId,
            "userId", userId,
            "action", action,
            "timestamp", System.currentTimeMillis()
        );

        redisTemplate.opsForList().leftPush(LIKE_SYNC_QUEUE_KEY, syncData);
    }

    private String getCurrentHour() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
    }
}
