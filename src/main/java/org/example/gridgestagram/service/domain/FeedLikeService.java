package org.example.gridgestagram.service.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.FeedLikeStatus;
import org.example.gridgestagram.controller.feed.dto.FeedLikeUserInfo;
import org.example.gridgestagram.controller.feed.dto.FeedLikeUserResponse;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.facade.RedisLikeFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedLikeService {

    private final UserService userService;
    private final FeedCacheService feedCacheService;
    private final RedisLikeFacade redisLikeFacade;
    private final RateLimiterService rateLimiterService;

    @Transactional
    public LikeToggleResponse toggleLike(Long feedId, Long userId) {
        if (!rateLimiterService.isLikeToggleAllowed(userId)
            || !rateLimiterService.isFeedLikeAllowed(userId, feedId)) {
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS);
        }
        if (!feedCacheService.feedExists(feedId)) {
            throw new CustomException(ErrorCode.FEED_NOT_FOUND);
        }
        return redisLikeFacade.toggleLike(feedId, userId);
    }

    @Transactional
    public FeedLikeStatus getFeedLikeStatus(Long feedId, Long userId) {
        if (!feedCacheService.feedExists(feedId)) {
            throw new CustomException(ErrorCode.FEED_NOT_FOUND);
        }

        Integer likeCount = redisLikeFacade.getLikeCount(feedId);
        boolean isLiked = redisLikeFacade.isLikedByUser(feedId, userId);

        return FeedLikeStatus.builder()
            .feedId(feedId)
            .liked(isLiked)
            .likeCount(likeCount)
            .build();
    }

    @Transactional
    public Page<FeedLikeUserResponse> getFeedLikeUsers(Long feedId, Pageable pageable) {
        if (!feedCacheService.feedExists(feedId)) {
            throw new CustomException(ErrorCode.FEED_NOT_FOUND);
        }

        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        List<FeedLikeUserInfo> userInfos = redisLikeFacade.getFeedLikeUsers(feedId, offset,
            limit);

        if (userInfos.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> userIds = userInfos.stream()
            .map(FeedLikeUserInfo::getUserId)
            .toList();

        if (userIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<User> users = userService.findUsersByIds(userIds);
        List<FeedLikeUserResponse> responses = users.stream()
            .map(user -> FeedLikeUserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .profileImageUrl(user.getProfileImageUrl())
                .likedAt(LocalDateTime.now())
                .build())
            .toList();

        long total = redisLikeFacade.getLikeCount(feedId);
        return new PageImpl<>(responses, pageable, total);
    }
}
