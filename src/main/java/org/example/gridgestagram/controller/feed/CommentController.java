package org.example.gridgestagram.controller.feed;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.feed.dto.CommentCreateRequest;
import org.example.gridgestagram.controller.feed.dto.CommentResponse;
import org.example.gridgestagram.controller.feed.dto.ReportRequest;
import org.example.gridgestagram.controller.feed.dto.ReportResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.ReportService;
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

@Tag(name = "댓글", description = "피드 댓글 작성, 조회, 삭제 및 신고 관련 API")
@RestController
@RequestMapping("/api/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacade commentFacade;
    private final ReportService reportService;

    @Operation(
        summary = "댓글 작성",
        description = "피드에 댓글을 작성합니다. 댓글 내용은 1-1000자 이내로 입력해야 합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "댓글 작성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"content\": \"좋은 사진이네요!\", \"user\": {\"id\": 2, \"username\": \"commenter123\", \"name\": \"김댓글\"}, \"createdAt\": \"2024-01-01 10:00:00\", \"updatedAt\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (내용 비어있음, 글자 수 초과 등)",
            content = @Content(mediaType = "application/json")
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
    @LogAction(value = LogType.COMMENT_CREATE, targetType = "COMMENT")
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
        @Parameter(description = "댓글을 작성할 피드 ID", example = "1")
        @PathVariable Long feedId,
        @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentFacade.createComment(feedId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "댓글 목록 조회",
        description = "피드의 댓글 목록을 페이징으로 조회합니다. 기본 10개씩, 최신 순으로 정렬됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "댓글 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"content\": [{\"id\": 1, \"content\": \"좋은 사진이네요!\", \"user\": {\"username\": \"user123\"}, \"createdAt\": \"2024-01-01 10:00:00\"}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 10}, \"totalElements\": 25}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.COMMENT_VIEW)
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
        @Parameter(description = "댓글을 조회할 피드 ID", example = "1")
        @PathVariable Long feedId,
        @Parameter(description = "페이지 정보 (기본 10개씩, 최신순)")
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable) {

        Page<CommentResponse> response = commentFacade.getComments(feedId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "댓글 삭제",
        description = "댓글을 삭제합니다. 댓글 작성자만 삭제할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "댓글 삭제 성공 (내용 없음)"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 (작성자가 아님)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "댓글을 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.COMMENT_DELETE, targetType = "COMMENT")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @Parameter(description = "삭제할 댓글 ID", example = "1")
        @PathVariable Long commentId,
        @Parameter(description = "피드 ID", example = "1")
        @PathVariable Long feedId) {
        commentFacade.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "댓글 신고",
        description = "부적절한 댓글을 신고합니다. 신고 사유를 선택하여 신고할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "댓글 신고 접수 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReportResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"type\": \"COMMENT\", \"targetId\": 1, \"content\": \"신고된 댓글 내용\", \"reporter\": {\"username\": \"reporter123\"}, \"writer\": {\"username\": \"commenter123\"}, \"reason\": \"INAPPROPRIATE_CONTENT\", \"status\": \"PENDING\", \"createdAt\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (신고 사유 누락 등)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "댓글을 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 신고한 댓글",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.REPORT_CREATE, targetType = "REPORT")
    @PostMapping("/{commentId}/reports")
    public ResponseEntity<ReportResponse> reportComment(
        @Parameter(description = "신고할 댓글 ID", example = "1")
        @PathVariable Long commentId,
        @Valid @RequestBody ReportRequest request) {

        ReportResponse response = reportService.report(request.getType(), commentId,
            request.getReason());
        return ResponseEntity.ok(response);
    }
}
