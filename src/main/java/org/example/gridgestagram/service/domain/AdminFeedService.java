package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.admin.dto.AdminFeedDetailResponse;
import org.example.gridgestagram.controller.admin.dto.AdminFeedResponse;
import org.example.gridgestagram.controller.admin.dto.AdminFeedSearchCondition;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.FeedRepository;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFeedService {

    private final FeedRepository feedRepository;

    public Page<AdminFeedResponse> searchFeeds(AdminFeedSearchCondition condition,
        Pageable pageable) {
        try {
            Page<Feed> feedPage = feedRepository.searchFeeds(condition, pageable);
            return feedPage.map(AdminFeedResponse::from);
        } catch (Exception e) {
            log.error("관리자 피드 검색 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.ADMIN_FEED_SEARCH_FAILED);
        }
    }

    public AdminFeedDetailResponse getFeedDetail(Long feedId) {
        try {
            Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
            return AdminFeedDetailResponse.from(feed);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("관리자 피드 상세 조회 실패: feedId={}", feedId, e);
            throw new CustomException(ErrorCode.ADMIN_FEED_DETAIL_FAILED);
        }
    }

    @Transactional
    public void deleteFeed(Long feedId, String reason) {
        try {
            Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

            feed.deleteByAdmin(reason);

            log.info("관리자가 피드를 삭제했습니다. feedId={}, reason={}", feedId, reason);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("관리자 피드 삭제 실패: feedId={}", feedId, e);
            throw new CustomException(ErrorCode.ADMIN_FEED_DELETE_FAILED);
        }
    }

}
