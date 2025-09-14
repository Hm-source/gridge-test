package org.example.gridgestagram.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.admin.dto.ReportProcessRequest;
import org.example.gridgestagram.controller.admin.dto.ReportQueryDto;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 - 신고 관리", description = "관리자용 신고 조회, 처리, 삭제 관리 API")
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminReportController {

    private final ReportService reportService;

    @Operation(
        summary = "신고 목록 조회",
        description = "관리자가 신고 목록을 조회합니다. 상태, 신고자, 작성자, 기간 등으로 필터링할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "신고 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"content\": [{\"id\": 1, \"type\": \"FEED\", \"targetId\": 123, \"reporter\": \"reporter123\", \"writer\": \"writer123\", \"reason\": \"INAPPROPRIATE_CONTENT\", \"status\": \"PENDING\", \"createdAt\": \"2024-01-01 10:00:00\"}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 20}, \"totalElements\": 50}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.ADMIN_REPORT_VIEW, targetType = "REPORT")
    @GetMapping
    public ResponseEntity<Page<ReportQueryDto>> getReports(
        @Parameter(description = "페이징 정보 (기본 20개씩, 최신순)")
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @Parameter(description = "신고 처리 상태 필터 (PENDING, APPROVED, REJECTED)", example = "PENDING")
        @RequestParam(required = false) ReportStatus status,
        @Parameter(description = "신고자 이름 필터", example = "reporter123")
        @RequestParam(required = false) String reporterName,
        @Parameter(description = "작성자 이름 필터", example = "writer123")
        @RequestParam(required = false) String writerName,
        @Parameter(description = "검색 시작 날짜 (YYYY-MM-DD 형식)", example = "2024-01-01")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "검색 종료 날짜 (YYYY-MM-DD 형식)", example = "2024-12-31")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Page<ReportQueryDto> reports = reportService.getReports(pageable, status, reporterName,
            writerName, startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @Operation(
        summary = "신고 처리",
        description = "관리자가 신고를 처리합니다. 신고를 승인하거나 반려할 수 있으며, 처리 사유를 명시할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "신고 처리 성공"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효성 검증 실패)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "신고를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.ADMIN_REPORT_HANDLE, targetType = "REPORT")
    @PutMapping("/{reportId}")
    public ResponseEntity<Void> processReport(
        @Parameter(description = "처리할 신고 ID", example = "1")
        @PathVariable Long reportId,
        @Valid @RequestBody ReportProcessRequest request) {

        reportService.processReport(reportId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "신고 삭제",
        description = "관리자가 신고를 삭제합니다. 잠복되었거나 부적절한 신고를 제거할 때 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "신고 삭제 성공"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "신고를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.ADMIN_REPORT_HANDLE, targetType = "REPORT")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(
        @Parameter(description = "삭제할 신고 ID", example = "1")
        @PathVariable Long reportId,
        @Valid @RequestBody ReportProcessRequest request) {

        reportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }


}

