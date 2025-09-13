package org.example.gridgestagram.controller.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.feed.dto.FeedLikeStatus;
import org.example.gridgestagram.controller.feed.dto.FeedLikeUserResponse;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.AuthenticationService;
import org.example.gridgestagram.service.domain.FeedLikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedLikeController {

    private final FeedLikeService feedLikeService;
    private final AuthenticationService authenticationService;

    @LogAction(value = LogType.FEED_LIKE, targetType = "FEED")
    @PostMapping("/{feedId}/likes")
    public ResponseEntity<LikeToggleResponse> toggleLike(
        @PathVariable Long feedId) {

        User user = authenticationService.getCurrentUser();
        log.info("좋아요 토글 요청 - 피드: {}, 사용자: {}", feedId, user.getUsername());
        LikeToggleResponse response = feedLikeService.toggleLike(feedId, user.getId());
        return ResponseEntity.ok(response);
    }

    @LogAction(value = LogType.FEED_LIKE_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}/likes/status")
    public ResponseEntity<FeedLikeStatus> getLikeStatus(
        @PathVariable Long feedId) {

        User user = authenticationService.getCurrentUser();
        FeedLikeStatus status = feedLikeService.getFeedLikeStatus(feedId, user.getId());

        return ResponseEntity.ok(status);
    }

    @LogAction(value = LogType.FEED_LIKE_USERS_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}/likes/users")
    public ResponseEntity<Page<FeedLikeUserResponse>> getFeedLikeUsers(
        @PathVariable Long feedId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedLikeUserResponse> users = feedLikeService.getFeedLikeUsers(feedId, pageable);

        return ResponseEntity.ok(users);
    }
}
