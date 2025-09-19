package org.example.gridgestagram.service.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.FeedLikeStatus;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.example.gridgestagram.controller.feed.dto.UserProjection;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.FeedLikeRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.facade.RedisLikeFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedLikeService {

    private final FeedCacheService feedCacheService;
    private final RedisLikeFacade redisLikeFacade;
    private final RateLimiterService rateLimiterService;
    private final FeedLikeRepository feedLikeRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public LikeToggleResponse toggleLike(Long feedId, Long userId) {
        if (!rateLimiterService.isFeedLikeAllowed(userId, feedId)) {
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

    public List<UserSimpleResponse> getFeedLikeUsers(Long feedId) {
        User currentUser = authenticationService.getCurrentUser();
        List<UserSimpleResponse> result = new ArrayList<>();

        boolean currentUserLiked = feedLikeRepository.existsByFeedIdAndUserId(feedId,
            currentUser.getId());
        if (currentUserLiked) {
            result.add(UserSimpleResponse.from(currentUser));
        }

        int remainingLimit = currentUserLiked ? 99 : 100;
        List<UserProjection> projections = feedLikeRepository.findRandomLikeUsersNative(feedId,
            remainingLimit);

        return projections.stream()
            .map(proj -> UserSimpleResponse.builder()
                .id(proj.getId())
                .username(proj.getUsername())
                .name(proj.getName())
                .profileImageUrl(proj.getProfileImageUrl())
                .role(proj.getRole())
                .subscriptionStatus(proj.getSubscriptionStatus())
                .build())
            .toList();
    }
}
