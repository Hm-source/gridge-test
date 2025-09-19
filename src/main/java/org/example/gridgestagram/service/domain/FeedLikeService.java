package org.example.gridgestagram.service.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.FeedLikeStatus;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.FeedLikeRepository;
import org.example.gridgestagram.repository.feed.entity.FeedLike;
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

    @Transactional(readOnly = true)
    public List<UserSimpleResponse> getFeedLikeUsers(Long feedId) {
        List<FeedLike> result = new ArrayList<>();
        User user = authenticationService.getCurrentUser();
        feedLikeRepository.findByFeedIdAndUserId(feedId, user.getId())
            .ifPresent(result::add);

        List<FeedLike> sampleLikes = feedLikeRepository.findRandomLikes(feedId, 100);
        result.addAll(sampleLikes);

        return result.stream()
            .map(like -> UserSimpleResponse.from(like.getUser()))
            .toList();
    }
}
