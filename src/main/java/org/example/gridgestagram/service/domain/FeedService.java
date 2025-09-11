package org.example.gridgestagram.service.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.CommentResponse;
import org.example.gridgestagram.controller.feed.dto.FeedCreateRequest;
import org.example.gridgestagram.controller.feed.dto.FeedResponse;
import org.example.gridgestagram.controller.feed.dto.FeedUpdateRequest;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.CommentRepository;
import org.example.gridgestagram.repository.feed.FeedRepository;
import org.example.gridgestagram.repository.feed.entity.Comment;
import org.example.gridgestagram.repository.feed.entity.Feed;
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
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<FeedResponse> getFeeds(Pageable pageable) {
        pageable = PaginationUtils.validateAndAdjust(pageable);

        try {
            Page<Feed> feedPage = feedRepository.findByStatusActive(pageable);
            List<Long> feedIds = feedPage.getContent().stream()
                .map(Feed::getId)
                .toList();

            Map<Long, List<Comment>> commentsByFeedId = Collections.emptyMap();
            try {
                if (!feedIds.isEmpty()) {
                    List<Comment> allComments = commentRepository.findTop3CommentsByFeedIds(
                        feedIds);
                    commentsByFeedId = allComments.stream()
                        .collect(Collectors.groupingBy(comment -> comment.getFeed().getId()));
                }
            } catch (Exception e) {
                log.warn("댓글 조회 실패로 댓글 없이 게시물 목록을 반환합니다: {}", e.getMessage());
            }

            final Map<Long, List<Comment>> finalCommentsByFeedId = commentsByFeedId;
            return feedPage.map(feed -> {
                List<CommentResponse> recentComments = finalCommentsByFeedId
                    .getOrDefault(feed.getId(), Collections.emptyList())
                    .stream()
                    .map(CommentResponse::from)
                    .toList();
                return FeedResponse.fromWithComments(feed, recentComments);
            });
        } catch (Exception e) {
            log.error("게시물 목록 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.FEED_LIST_FETCH_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public Feed getFeed(Long feedId) {
        try {
            return feedRepository.findByIdAndStatusActive(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
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

    @Transactional
    public Feed deleteFeed(Long feedId, Long userId) {
        try {
            Feed feed = feedRepository.findByIdAndUserId(feedId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.FEED_NOT_FOUND)
            );
            feed.deleteByUser();
            return feed;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_DELETE_FAILED);
        }
    }

    @Transactional
    public Feed hideFeed(Long feedId, Long userId) {
        try {
            Feed feed = feedRepository.findByIdAndUserId(feedId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.FEED_NOT_FOUND)
            );
            feed.hide();
            return feed;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_HIDE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public Feed findById(Long feedId) {
        if (feedId == null || feedId <= 0) {
            throw new CustomException(ErrorCode.INVALID_FEED_ID);
        }
        try {
            return feedRepository.findByIdAndStatusActive(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_FETCH_FAILED);
        }
    }

    @Transactional
    public Feed updateFeed(Long feedId, Long userId, FeedUpdateRequest request) {
        try {
            Feed feed = feedRepository.findByIdAndUserId(feedId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.FEED_NOT_FOUND)
            );
            feed.update(request.getContent());
            return feed;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_UPDATE_FAILED);
        }
    }


}
