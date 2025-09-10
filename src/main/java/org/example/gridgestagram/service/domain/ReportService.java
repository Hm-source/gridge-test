package org.example.gridgestagram.service.domain;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.ReportProcessRequest;
import org.example.gridgestagram.controller.admin.dto.ReportQueryDto;
import org.example.gridgestagram.controller.feed.dto.ReportResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.CommentRepository;
import org.example.gridgestagram.repository.feed.FeedRepository;
import org.example.gridgestagram.repository.feed.ReportRepository;
import org.example.gridgestagram.repository.feed.ReportRepositoryImpl;
import org.example.gridgestagram.repository.feed.entity.Comment;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.feed.entity.Report;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepositoryImpl reportRepositoryImpl;
    private final AuthenticationService authenticationService;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final FeedRepository feedRepository;

    @Transactional(readOnly = true)
    public Page<ReportQueryDto> getReports(
        Pageable pageable,
        ReportStatus status,
        String reporterName,
        String writerName,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return reportRepositoryImpl.findReports(pageable, status, reporterName, writerName,
            startDate, endDate);
    }

    @Transactional
    public ReportResponse report(ReportType type, Long targetId, ReportReason reason) {
        User reporter = authenticationService.getCurrentUser();
        if (reportRepository.existsByTypeAndTargetIdAndReporterId(type, targetId,
            reporter.getId())) {
            throw new CustomException(ErrorCode.ALREADY_REPORTED);
        }
        if (type == ReportType.FEED) {
            Feed feed = feedRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
            Report report = Report.create(type, targetId, reporter, reason);
            reportRepository.save(report);
            return ReportResponse.from(report, feed.getContent(), feed.getUser());
        } else {
            Comment comment = commentRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
            Report report = Report.create(type, targetId, reporter, reason);
            reportRepository.save(report);
            return ReportResponse.from(report, comment.getContent(), comment.getUser());
        }
    }

    @Transactional
    public void processReport(Long reportId, ReportProcessRequest request) {
        User admin = authenticationService.getCurrentUser();

        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        if (request.isApprove()) {
            report.approve(admin);

            if (report.getType() == ReportType.FEED) {
                Feed feed = feedRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
                feed.hide();
            } else if (report.getType() == ReportType.COMMENT) {
                Comment comment = commentRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
                comment.hide();
            }
        }
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        reportRepository.delete(report);
    }
}
