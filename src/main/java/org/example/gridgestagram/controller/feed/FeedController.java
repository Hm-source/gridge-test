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
import org.example.gridgestagram.controller.feed.dto.FeedCreateRequest;
import org.example.gridgestagram.controller.feed.dto.FeedDetailResponse;
import org.example.gridgestagram.controller.feed.dto.FeedResponse;
import org.example.gridgestagram.controller.feed.dto.FeedUpdateRequest;
import org.example.gridgestagram.controller.feed.dto.ReportRequest;
import org.example.gridgestagram.controller.feed.dto.ReportResponse;
import org.example.gridgestagram.service.domain.ReportService;
import org.example.gridgestagram.service.facade.FeedFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "피드", description = "피드 작성, 조회, 수정, 삭제 및 신고 관련 API")
@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedFacade feedFacade;
    private final ReportService reportService;

    @Operation(
        summary = "피드 작성",
        description = "새로운 피드를 작성합니다. 텍스트 내용(1-1000자)과 이미지/비디오 파일(최대 10개)을 포함할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "피드 작성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FeedResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"content\": \"오늘의 일상입니다!\", \"status\": \"ACTIVE\", \"likeCount\": 0, \"commentCount\": 0, \"user\": {\"id\": 1, \"username\": \"user123\", \"name\": \"홍길동\"}, \"files\": [], \"createdAt\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (내용 비어있음, 파일 개수 초과 등)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (로그인 필요)",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping
    public ResponseEntity<FeedResponse> createFeed(
        @Valid @RequestBody FeedCreateRequest request) {
        FeedResponse response = feedFacade.createFeed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "피드 목록 조회",
        description = "전체 피드 목록을 페이징으로 조회합니다. 기본 10개씩, 최신 순으로 정렬됩니다. 각 피드에는 최근 댓글들이 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피드 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"content\": [{\"id\": 1, \"content\": \"오늘의 일상\", \"likeCount\": 5, \"commentCount\": 3, \"user\": {\"username\": \"user123\"}}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 10}, \"totalElements\": 25}"
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeeds(
        @Parameter(description = "페이지 정보 (기본 10개씩)")
        @PageableDefault(size = 10)
        Pageable pageable) {
        Page<FeedResponse> response = feedFacade.getFeeds(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "피드 상세 조회",
        description = "특정 피드의 상세 정보를 조회합니다. 피드 내용, 첨부파일, 작성자 정보, 좋아요/댓글 수 등을 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피드 상세 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FeedDetailResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"content\": \"오늘의 일상 사진입니다!\", \"status\": \"ACTIVE\", \"likeCount\": 15, \"commentCount\": 8, \"user\": {\"id\": 1, \"username\": \"user123\", \"name\": \"홍길동\"}, \"files\": [{\"id\": 1, \"url\": \"https://example.com/image.jpg\", \"order\": 0}], \"createdAt\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedDetailResponse> getFeed(
        @Parameter(description = "조회할 피드 ID", example = "1")
        @PathVariable Long feedId) {
        FeedDetailResponse response = feedFacade.getFeed(feedId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "피드 수정",
        description = "기존 피드의 내용을 수정합니다. 피드 작성자만 수정할 수 있습니다. 내용은 1-1000자 이내로 입력해야 합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피드 수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FeedResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (내용 비어있음 등)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 (작성자가 아님)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponse> updateFeed(
        @Parameter(description = "수정할 피드 ID", example = "1")
        @PathVariable Long feedId,
        @Valid @RequestBody FeedUpdateRequest request) {

        FeedResponse response = feedFacade.updateFeed(feedId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "피드 삭제",
        description = "피드를 삭제합니다. 피드 작성자만 삭제할 수 있습니다. 삭제 시 관련된 댓글, 좋아요, 첨부파일도 모두 삭제됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "피드 삭제 성공 (내용 없음)"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 (작성자가 아님)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
        @Parameter(description = "삭제할 피드 ID", example = "1")
        @PathVariable Long feedId) {
        feedFacade.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
        summary = "피드 신고",
        description = "부적절한 피드를 신고합니다. 신고 사유를 선택하여 신고할 수 있습니다. 신고는 관리자가 검토하여 처리합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "신고 접수 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReportResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"type\": \"FEED\", \"targetId\": 1, \"content\": \"신고된 피드 내용\", \"reporter\": {\"username\": \"reporter123\"}, \"writer\": {\"username\": \"writer123\"}, \"reason\": \"INAPPROPRIATE_CONTENT\", \"status\": \"PENDING\", \"createdAt\": \"2024-01-01 10:00:00\"}"
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
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 신고한 피드",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/{feedId}/reports")
    public ResponseEntity<ReportResponse> reportFeed(
        @Parameter(description = "신고할 피드 ID", example = "1")
        @PathVariable Long feedId,
        @Valid @RequestBody ReportRequest request) {

        ReportResponse response = reportService.report(request.getType(), feedId,
            request.getReason());
        return ResponseEntity.ok(response);
    }
}
