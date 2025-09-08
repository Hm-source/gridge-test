package org.example.gridgestagram.controller.feed;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.feed.dto.CommentCreateRequest;
import org.example.gridgestagram.controller.feed.dto.CommentResponse;
import org.example.gridgestagram.service.facade.CommentFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacade commentFacade;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
        @PathVariable Long feedId,
        @Valid @RequestBody CommentCreateRequest request) {

        CommentResponse response = commentFacade.createComment(feedId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
        @PathVariable Long feedId,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable) {

        Page<CommentResponse> response = commentFacade.getComments(feedId, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Long commentId, @PathVariable Long feedId) {
        commentFacade.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
