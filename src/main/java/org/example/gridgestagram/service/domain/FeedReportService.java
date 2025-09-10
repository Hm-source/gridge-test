package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.ReportProcessRequest;
import org.example.gridgestagram.controller.feed.dto.FeedReportRequest;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedReportService {

    private final FeedReportRepository feedReportRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public void reportFeed(Feed feed, FeedReportRequest request, User reporter) {
        try {
            if (feedReportRepository.existsByFeedIdAndReporterId(feed.getId(), reporter.getId())) {
                throw new CustomException(ErrorCode.ALREADY_REPORTED);
            }

            FeedReport report = FeedReport.create(feed, reporter, request.getReason(),
                request.getDescription());
            feedReportRepository.save(report);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEED_REPORT_FAILED);
        }
    }

    @Transactional
    public void processReport(Long reportId, ReportProcessRequest request) {
        User admin = authenticationService.getCurrentUser();
        FeedReport report = feedReportRepository.findById(reportId)
            .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        if (request.isApprove()) {
            report.approve(admin);
            report.getFeed().hide(); // 숨김 처리 -> 논리 삭제
        }
    }

    @Transactional
    public Page<FeedReportResponse> getReports(Pageable pageable, ReportStatus status) {
        Page<FeedReport> reportPage;

        if (status != null) {
            reportPage = feedReportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            reportPage = feedReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return reportPage.map(FeedReportResponse::from);
    }

    @Transactional
    public void deleteReport(Long reportId) {
        feedReportRepository.findById(reportId)
            .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
        feedReportRepository.deleteById(reportId);
    }
}
