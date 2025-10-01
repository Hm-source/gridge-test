package org.example.gridgestagram.service.facade;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLikeFacade {

    private static final String FEED_LIKE_COUNT_KEY = "feed:like:count:%d";
    private static final String FEED_LIKE_USERS_KEY = "feed:like:users:%d";
    private static final String LIKE_SYNC_QUEUE_KEY = "like:sync:queue";

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeToggleResponse toggleLike(Long feedId, Long userId) {
        String likeCountKey = String.format(FEED_LIKE_COUNT_KEY, feedId);
        String likeUsersKey = String.format(FEED_LIKE_USERS_KEY, feedId);

        Double score = stringRedisTemplate.opsForZSet().score(likeUsersKey, userId.toString());
        Boolean isLiked = (score != null);

        if (Boolean.TRUE.equals(isLiked)) {
            return removeLike(feedId, userId, likeCountKey, likeUsersKey);
        } else {
            return addLike(feedId, userId, likeCountKey, likeUsersKey);
        }
    }

    private LikeToggleResponse addLike(Long feedId, Long userId, String likeCountKey,
        String likeUsersKey) {

        try {
            long timestamp = System.currentTimeMillis();
            List<Object> results = stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();

                    operations.opsForValue().increment(likeCountKey, 1);
                    operations.opsForZSet().add(likeUsersKey, userId.toString(), timestamp);

                    return operations.exec();
                }
            });

            if (results == null || results.isEmpty()) {
                log.warn("좋아요 추가 실패 - 피드: {}, 사용자: {}", feedId, userId);
                throw new CustomException(ErrorCode.LIKE_REDIS_TRANSACTION_FAILED);
            }

            addToSyncQueue(feedId, userId, "ADD");

            Integer likeCount = getLikeCount(feedId);

            log.info("좋아요 추가 완료 - 피드: {}, 사용자: {}, 현재 좋아요 수: {}", feedId, userId, likeCount);

            return LikeToggleResponse.builder()
                .liked(true)
                .likeCount(likeCount)
                .message("좋아요를 눌렀습니다.")
                .timestamp(LocalDateTime.now())
                .build();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private LikeToggleResponse removeLike(Long feedId, Long userId, String likeCountKey,
        String likeUsersKey) {
        try {
            List<Object> results = stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();

                    operations.opsForValue().decrement(likeCountKey, 1);
                    operations.opsForZSet().remove(likeUsersKey, userId.toString());

                    return operations.exec();
                }
            });

            if (results != null && !results.isEmpty()) {
                addToSyncQueue(feedId, userId, "REMOVE");
            } else {
                throw new CustomException(ErrorCode.LIKE_REDIS_TRANSACTION_FAILED);
            }

            Integer likeCount = Math.max(0, getLikeCount(feedId));

            log.info("좋아요 취소 완료 - 피드: {}, 사용자: {}, 현재 좋아요 수: {}", feedId, userId, likeCount);

            return LikeToggleResponse.builder()
                .liked(false)
                .likeCount(likeCount)
                .message("좋아요를 취소했습니다.")
                .timestamp(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }


    public Integer getLikeCount(Long feedId) {
        String likeCountKey = String.format(FEED_LIKE_COUNT_KEY, feedId);
        String countStr = stringRedisTemplate.opsForValue().get(likeCountKey);
        return countStr != null ? Integer.parseInt(countStr) : 0;
    }

    public Map<Long, Integer> getLikeCounts(List<Long> feedIds) {
        List<String> keys = feedIds.stream()
            .map(id -> String.format(FEED_LIKE_COUNT_KEY, id))
            .toList();

        List<String> counts = stringRedisTemplate.opsForValue().multiGet(keys);

        Map<Long, Integer> result = new HashMap<>();
        for (int i = 0; i < feedIds.size(); i++) {
            String countStr = counts.get(i);
            result.put(feedIds.get(i), countStr != null ? Integer.parseInt(countStr) : 0);
        }
        return result;
    }

    public boolean isLikedByUser(Long feedId, Long userId) {
        String likeUsersKey = String.format(FEED_LIKE_USERS_KEY, feedId);
        Double score = stringRedisTemplate.opsForZSet().score(likeUsersKey, userId.toString());
        return score != null;
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
}
