package org.example.gridgestagram.controller.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.LikePair;
import org.example.gridgestagram.repository.feed.FeedLikeRepository;
import org.example.gridgestagram.repository.feed.entity.FeedLike;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeSyncScheduler {

    private static final String LIKE_SYNC_QUEUE_KEY = "like:sync:queue";
    private static final int BATCH_SIZE = 100;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedLikeRepository feedLikeRepository;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void processSyncQueue() {
        List<LikeAction> actions = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Map<String, Object> item = (Map<String, Object>)
                redisTemplate.opsForList().rightPop(LIKE_SYNC_QUEUE_KEY);
            if (item == null) {
                break;
            }

            actions.add(new LikeAction(
                Long.valueOf(item.get("feedId").toString()),
                Long.valueOf(item.get("userId").toString()),
                item.get("action").toString(),
                Long.valueOf(item.get("timestamp").toString())
            ));
        }

        if (actions.isEmpty()) {
            return;
        }

        Map<String, LikeAction> finalActions = new HashMap<>();
        for (LikeAction action : actions) {
            String key = action.feedId + "_" + action.userId;

            LikeAction existing = finalActions.get(key);
            if (existing == null || action.timestamp > existing.timestamp) {
                finalActions.put(key, action);
            }
        }

        log.info("좋아요 동기화: {} 개 액션 → {} 개 최종 액션", actions.size(), finalActions.size());

        List<LikeAction> addActions = new ArrayList<>();
        List<LikeAction> removeActions = new ArrayList<>();

        for (LikeAction action : finalActions.values()) {
            if ("ADD".equals(action.action)) {
                addActions.add(action);
            } else if ("REMOVE".equals(action.action)) {
                removeActions.add(action);
            }
        }

        if (!addActions.isEmpty()) {
            processBatchAdd(addActions);
        }
        if (!removeActions.isEmpty()) {
            processBatchRemove(removeActions);
        }
    }

    private void processBatchAdd(List<LikeAction> addActions) {
        List<Long> feedIds = addActions.stream().map(a -> a.feedId).collect(Collectors.toList());
        List<Long> userIds = addActions.stream().map(a -> a.userId).collect(Collectors.toList());

        List<LikePair> pairs = feedLikeRepository.findExistingPairs(feedIds, userIds);
        Set<String> existingPairs = Optional.ofNullable(pairs)
            .orElse(Collections.emptyList())
            .stream()
            .map(pair -> pair.feedId() + "_" + pair.userId())
            .collect(Collectors.toSet());

        List<FeedLike> newLikes = addActions.stream()
            .filter(action -> !existingPairs.contains(action.feedId + "_" + action.userId))
            .map(action -> FeedLike.create(action.feedId, action.userId))
            .collect(Collectors.toList());

        if (!newLikes.isEmpty()) {
            try {
                feedLikeRepository.saveAll(newLikes);
                log.info("좋아요 {} 개 배치 추가", newLikes.size());
            } catch (Exception e) {
                log.error("배치 추가 실패", e);
            }
        }
    }

    private void processBatchRemove(List<LikeAction> removeActions) {
        List<Long> feedIds = removeActions.stream().map(a -> a.feedId).collect(Collectors.toList());
        List<Long> userIds = removeActions.stream().map(a -> a.userId).collect(Collectors.toList());

        int deletedCount = feedLikeRepository.batchDeleteByFeedIdsAndUserIds(feedIds, userIds);
        log.info("좋아요 {} 개 배치 삭제", deletedCount);
    }

    private static class LikeAction {

        Long feedId;
        Long userId;
        String action;
        Long timestamp;

        LikeAction(Long feedId, Long userId, String action, Long timestamp) {
            this.feedId = feedId;
            this.userId = userId;
            this.action = action;
            this.timestamp = timestamp;
        }
    }

}