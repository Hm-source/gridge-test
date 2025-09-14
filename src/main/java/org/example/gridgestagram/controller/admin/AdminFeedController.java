package org.example.gridgestagram.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.admin.dto.AdminFeedDetailResponse;
import org.example.gridgestagram.controller.admin.dto.AdminFeedResponse;
import org.example.gridgestagram.controller.admin.dto.AdminFeedSearchCondition;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.AdminFeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 - 피드 관리", description = "관리자용 피드 검색, 상세 조회, 삭제 관리 API")
@RestController
@RequestMapping("/api/admin/feeds")
@RequiredArgsConstructor
public class AdminFeedController {

    private final AdminFeedService adminFeedService;

    @Operation(
        summary = "피드 검색 및 목록 조회",
        description = "관리자가 다양한 조건으로 피드를 검색하고 목록을 조회합니다. 사용자명, 상태, 기간 등으로 필터링할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피드 검색 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"content\": [{\"id\": 1, \"content\": \"피드 내용\", \"status\": \"ACTIVE\", \"likeCount\": 10, \"commentCount\": 5, \"user\": {\"username\": \"user123\"}, \"createdAt\": \"2024-01-01 10:00:00\"}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 20}, \"totalElements\": 100}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        )
    })
    @Secured("ROLE_ADMIN")
    @LogAction(value = LogType.ADMIN_FEED_VIEW, targetType = "FEED")
    @GetMapping
    public ResponseEntity<Page<AdminFeedResponse>> searchFeeds(
        @Parameter(description = "검색 조건 (사용자명, 상태, 기간 등)")
        @ModelAttribute AdminFeedSearchCondition condition,
        @Parameter(description = "페이징 정보 (기본 20개씩, 최신순)")
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdminFeedResponse> result = adminFeedService.searchFeeds(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "피드 상세 조회",
        description = "관리자가 특정 피드의 상세 정보를 조회합니다. 작성자 정보, 첨부파일, 신고 내역 등을 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피드 상세 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AdminFeedDetailResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"content\": \"상세 피드 내용\", \"status\": \"ACTIVE\", \"likeCount\": 15, \"commentCount\": 8, \"user\": {\"id\": 1, \"username\": \"user123\", \"email\": \"user@example.com\"}, \"files\": [], \"reports\": [], \"createdAt\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @Secured("ROLE_ADMIN")
    @LogAction(value = LogType.ADMIN_FEED_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}")
    public ResponseEntity<AdminFeedDetailResponse> getFeedDetail(
        @Parameter(description = "상세 조회할 피드 ID", example = "1")
        @PathVariable Long feedId) {
        AdminFeedDetailResponse response = adminFeedService.getFeedDetail(feedId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "피드 삭제",
        description = "관리자가 문제가 있는 피드를 삭제합니다. 삭제 사유를 명시할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "피드 삭제 성공 (내용 없음)"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "피드를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @Secured("ROLE_ADMIN")
    @LogAction(value = LogType.ADMIN_FEED_DELETE, targetType = "FEED")
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
        @Parameter(description = "삭제할 피드 ID", example = "1")
        @PathVariable Long feedId,
        @Parameter(description = "삭제 사유 (선택사항)", example = "스팸 콘텐츠로 인한 삭제")
        @RequestParam(required = false) String reason) {
        adminFeedService.deleteFeed(feedId, reason);
        return ResponseEntity.noContent().build();
    }
}
