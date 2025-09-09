package org.example.gridgestagram.service.facade;

import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.feed.dto.CommentCreateRequest;
import org.example.gridgestagram.controller.feed.dto.CommentResponse;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.AuthenticationService;
import org.example.gridgestagram.service.domain.CommentService;
import org.example.gridgestagram.service.domain.FeedService;
import org.example.gridgestagram.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentFacade {

    private final AuthenticationService authenticationService;
    private final CommentService commentService;
    private final FeedService feedService;

    @Transactional
    public CommentResponse createComment(Long feedId, CommentCreateRequest request) {
        User user = authenticationService.getCurrentUser();
        return commentService.createComment(user, feedId, request);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long feedId, Pageable pageable) {
        feedService.findById(feedId);
        pageable = PaginationUtils.validateAndAdjust(pageable);
        return commentService.getComments(feedId, pageable);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User user = authenticationService.getCurrentUser();
        commentService.deleteComment(commentId, user.getId());
    }

}
