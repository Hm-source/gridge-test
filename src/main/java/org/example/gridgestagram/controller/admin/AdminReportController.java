package org.example.gridgestagram.controller.admin;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.ReportProcessRequest;
import org.example.gridgestagram.controller.admin.dto.ReportQueryDto;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
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

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Page<ReportQueryDto>> getReports(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(required = false) ReportStatus status,
        @RequestParam(required = false) String reporterName,
        @RequestParam(required = false) String writerName,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Page<ReportQueryDto> reports = reportService.getReports(pageable, status, reporterName,
            writerName, startDate, endDate);
        return ResponseEntity.ok(reports);
    }


    @PutMapping("/{reportId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> processReport(
        @PathVariable Long reportId,
        @Valid @RequestBody ReportProcessRequest request) {

        reportService.processReport(reportId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reportId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteReport(
        @PathVariable Long reportId,
        @Valid @RequestBody ReportProcessRequest request) {

        reportService.deleteReport(reportId);
        return ResponseEntity.ok().build();
    }


}

