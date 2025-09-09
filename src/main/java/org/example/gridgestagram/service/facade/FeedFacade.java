package org.example.gridgestagram.service.facade;

import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.feed.dto.FeedCreateRequest;
import org.example.gridgestagram.controller.feed.dto.FeedDetailResponse;
import org.example.gridgestagram.controller.feed.dto.FeedResponse;
import org.example.gridgestagram.controller.feed.dto.FeedUpdateRequest;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.AuthenticationService;
import org.example.gridgestagram.service.domain.FeedService;
import org.example.gridgestagram.service.domain.FilesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedFacade {

    private final FeedService feedService;
    private final FilesService filesService;
    private final AuthenticationService authenticationService;

    @Transactional
    public Page<FeedResponse> getFeeds(Pageable pageable) {
        return feedService.getFeeds(pageable);
    }

    @Transactional
    public FeedResponse createFeed(FeedCreateRequest request) {
        if (request == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        User user = authenticationService.getCurrentUser();
        Feed savedFeed = feedService.createFeed(user, request);
        filesService.saveFiles(savedFeed, request.getFiles());
        return FeedResponse.from(savedFeed);

    }

    @Transactional(readOnly = true)
    public FeedDetailResponse getFeed(Long feedId) {
        return FeedDetailResponse.from(feedService.getFeed(feedId));
    }

    @Transactional
    public FeedResponse updateFeed(Long feedId, FeedUpdateRequest request) {
        User user = authenticationService.getCurrentUser();
        Feed feed = feedService.updateFeed(feedId, user.getId(), request);
        return FeedResponse.from(feed);
    }

    @Transactional
    public void deleteFeed(Long feedId) {
        User user = authenticationService.getCurrentUser();
        feedService.deleteFeed(feedId, user.getId());
    }

}
