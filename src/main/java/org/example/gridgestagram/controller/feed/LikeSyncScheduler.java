package org.example.gridgestagram.controller.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.repository.feed.FeedLikeRepository;
import org.example.gridgestagram.repository.feed.FeedRepository;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.feed.entity.FeedLike;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeSyncScheduler {

    private static final String LIKE_SYNC_QUEUE_KEY = "like:sync:queue";
    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedRepository feedRepository;
    private final UserService userService;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void syncLikesToDatabase() {
        try {
            int batchSize = 100;
            List<Object> syncItems = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                Object item = redisTemplate.opsForList().rightPop(LIKE_SYNC_QUEUE_KEY);
                if (item == null) {
                    break;
                }
                syncItems.add(item);
            }

            if (syncItems.isEmpty()) {
                return;
            }

            log.info("좋아요 DB 동기화 시작 - 처리할 항목: {}개", syncItems.size());

            for (Object item : syncItems) {
                try {
                    processSyncItem((Map<String, Object>) item);
                } catch (Exception e) {
                    log.error("좋아요 동기화 실패: {}", item, e);
                }
            }

            log.info("좋아요 DB 동기화 완료 - 처리된 항목: {}개", syncItems.size());

        } catch (Exception e) {
            log.error("좋아요 동기화 스케줄러 오류", e);
        }
    }

    private void processSyncItem(Map<String, Object> item) {
        Long feedId = Long.valueOf(item.get("feedId").toString());
        Long userId = Long.valueOf(item.get("userId").toString());
        String action = item.get("action").toString();

        Feed feed = feedRepository.findById(feedId).orElse(null);
        if (feed == null) {
            log.warn("존재하지 않는 피드 ID: {}", feedId);
            return;
        }

        User user = userService.findById(userId);

        if ("ADD".equals(action)) {
            if (!feedLikeRepository.existsByFeedIdAndUserId(feedId, userId)) {
                FeedLike feedLike = FeedLike.create(feed, user);
                feedLikeRepository.save(feedLike);

                feed.increaseLikeCount();
                feedRepository.save(feed);
            }
        } else if ("REMOVE".equals(action)) {
            feedLikeRepository.deleteByFeedIdAndUserId(feedId, userId);

            feed.decreaseLikeCount();
            feedRepository.save(feed);
        }
    }

}