package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.feed.entity.FeedReport;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;

@Getter
@Builder
public class FeedReportResponse {

    private Long id;
    private Long feedId;
    private String feedContent;
    private UserSimpleResponse reporter;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private UserSimpleResponse processedBy;

    public static FeedReportResponse from(FeedReport report) {
        return FeedReportResponse.builder()
            .id(report.getId())
            .feedId(report.getFeed().getId())
            .feedContent(report.getFeed().getContent())
            .reporter(UserSimpleResponse.from(report.getReporter()))
            .reason(report.getReason())
            .description(report.getDescription())
            .status(report.getStatus())
            .createdAt(report.getCreatedAt())
            .processedAt(report.getProcessedAt())
            .processedBy(report.getProcessedBy() != null ?
                UserSimpleResponse.from(report.getProcessedBy()) : null)
            .build();
    }
}