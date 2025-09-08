package org.example.gridgestagram.service.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.FeedCreateRequest;
import org.example.gridgestagram.controller.feed.dto.FeedResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.FeedRepository;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.files.entity.Files;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final FeedRepository feedRepository;
    private final FilesService filesService;

    @Transactional(readOnly = true)
    public Page<FeedResponse> getFeeds(Pageable pageable) {
        pageable = PaginationUtils.validateAndAdjust(pageable);

        try {
            Page<Feed> feedPage = feedRepository.findByIsVisibleTrue(pageable);
            return feedPage.map(FeedResponse::from);
        } catch (Exception e) {
            log.error("게시물 목록 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.FEED_LIST_FETCH_FAILED);
        }
    }

    public FeedResponse getFeed(Long feedId) {
        if (feedId == null || feedId <= 0) {
            throw new CustomException(ErrorCode.INVALID_FEED_ID);
        }
        try {
            Feed feed = feedRepository.findByIdAndIsVisibleTrue(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

            return FeedResponse.from(feed);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_FETCH_FAILED);
        }
    }

    @Transactional
    public Feed createFeed(User user, FeedCreateRequest request) {
        try {
            Feed feed = Feed.create(user, request.getContent());
            return feedRepository.save(feed);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_CREATE_FAILED);
        }
    }

    public void deleteFeed(Long feedId, Long userId) {
        try {
            Feed feed = feedRepository.findByIdAndUserId(feedId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.FEED_NOT_FOUND)
            );
            feed.hide();

            List<String> fileUrls = feed.getFiles().stream()
                .map(Files::getUrl)
                .toList();

            if (!fileUrls.isEmpty()) {
//                deleteFilesFromS3Async(fileUrls);
            }

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_DELETE_FAILED);
        }
    }
}
