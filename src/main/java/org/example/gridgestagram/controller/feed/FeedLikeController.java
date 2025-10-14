package org.example.gridgestagram.controller.feed;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.feed.dto.FeedLikeStatus;
import org.example.gridgestagram.controller.feed.dto.LikeToggleResponse;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.AuthenticationService;
import org.example.gridgestagram.service.domain.FeedLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "피드 좋아요", description = "피드 좋아요 추가/제거, 상태 조회, 좋아요 누른 사용자 목록 관련 API")
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedLikeController {

    private final FeedLikeService feedLikeService;
    private final AuthenticationService authenticationService;

    @Operation(
        summary = "좋아요 토글",
        description = "피드에 좋아요를 추가하거나 제거합니다. 이미 좋아요를 누른 경우 제거되고, 아니면 추가됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "좋아요 토글 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LikeToggleResponse.class),
                examples = @ExampleObject(
                    value = "{\"liked\": true, \"likeCount\": 15, \"message\": \"좋아요를 추가했습니다.\", \"timestamp\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (로그인 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "429",
            description = "요청 제한 초과 (Rate Limiting)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"요청이 너무 많습니다. 잠시 후 다시 시도해주세요.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        )
    })
    @LogAction(value = LogType.FEED_LIKE, targetType = "FEED")
    @PostMapping("/{feedId}/likes")
    public ResponseEntity<LikeToggleResponse> toggleLike(
        @Parameter(description = "좋아요를 토글할 피드 ID", example = "1")
        @PathVariable Long feedId) {

        User user = authenticationService.getCurrentUser();
        log.info("좋아요 토글 요청 - 피드: {}, 사용자: {}", feedId, user.getUsername());
        LikeToggleResponse response = feedLikeService.toggleLike(feedId, user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "좋아요 상태 조회",
        description = "현재 사용자가 해당 피드에 좋아요를 눌렀는지 여부와 전체 좋아요 수를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "좋아요 상태 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FeedLikeStatus.class),
                examples = @ExampleObject(
                    value = "{\"feedId\": 1, \"liked\": true, \"likeCount\": 25}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (로그인 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.FEED_LIKE_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}/likes/status")
    public ResponseEntity<FeedLikeStatus> getLikeStatus(
        @Parameter(description = "좋아요 상태를 조회할 피드 ID", example = "1")
        @PathVariable Long feedId) {

        User user = authenticationService.getCurrentUser();
        FeedLikeStatus status = feedLikeService.getFeedLikeStatus(feedId, user.getId());

        return ResponseEntity.ok(status);
    }

    @Operation(
        summary = "좋아요 누른 사용자 목록 조회",
        description = "피드에 좋아요를 누른 랜덤 사용자 100명을 조회됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "좋아요 누른 사용자 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"content\": [{\"userId\": 2, \"username\": \"user123\", \"profileImageUrl\": \"https://example.com/profile.jpg\"}]}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.FEED_LIKE_USERS_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}/likes/users")
    public ResponseEntity<List<UserSimpleResponse>> getFeedLikeUsers(
        @Parameter(description = "좋아요 누른 사용자를 조회할 피드 ID", example = "1")
        @PathVariable Long feedId) {

        List<UserSimpleResponse> users = feedLikeService.getFeedLikeUsers(feedId);

        return ResponseEntity.ok(users);
    }
}
