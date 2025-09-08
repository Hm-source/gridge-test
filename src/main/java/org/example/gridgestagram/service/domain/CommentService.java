package org.example.gridgestagram.service.domain;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.feed.dto.CommentCreateRequest;
import org.example.gridgestagram.controller.feed.dto.CommentResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final FeedService feedService;

    @Transactional
    public CommentResponse createComment(User user, Long feedId, CommentCreateRequest request) {
        try {
            Feed feed = feedService.findById(feedId);
            Comment comment = Comment.create(feed, user, request.getContent());
            Comment savedComment = commentRepository.save(comment);
            feed.incrementCommentCount();
            return CommentResponse.from(savedComment);

        } catch (CustomException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.COMMENT_CREATE_FAILED);
        }
    }

    public Page<CommentResponse> getComments(Long feedId, Pageable pageable) {
        try {
            pageable = PaginationUtils.validateAndAdjust(pageable);
            Page<Comment> commentPage = commentRepository.findByFeedIdOrderByCreatedAtDesc(feedId,
                pageable);
            return commentPage.map(CommentResponse::from);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.COMMENT_LIST_FETCH_FAILED);
        }
    }

    public List<CommentResponse> getRecentComments(Long feedId, int limit) {
        try {
            List<Comment> comments = commentRepository.findTopCommentsByFeedId(feedId, limit);

            return comments.stream()
                .map(CommentResponse::from)
                .toList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        try {
            Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

            comment.validateCommentOwner(comment, userId);
            Feed feed = comment.getFeed();
            commentRepository.delete(comment);
            feed.decrementCommentCount();

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_FAILED);
        }
    }
}
