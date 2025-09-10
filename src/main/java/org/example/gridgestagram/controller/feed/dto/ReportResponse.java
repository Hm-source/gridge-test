package org.example.gridgestagram.controller.feed.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.feed.entity.Report;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;
import org.example.gridgestagram.repository.user.entity.User;

@Getter
@Builder
public class ReportResponse {

    private Long id;
    private ReportType type;
    private Long targetId;
    private String content; // 피드 내용 또는 댓글 내용
    private UserSimpleResponse reporter;
    private UserSimpleResponse writer;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private UserSimpleResponse processedBy;

    public static ReportResponse from(Report report, String content, User writer) {
        return ReportResponse.builder()
            .id(report.getId())
            .type(report.getType())
            .targetId(report.getTargetId())
            .content(content)
            .reporter(UserSimpleResponse.from(report.getReporter()))
            .writer(UserSimpleResponse.from(writer))
            .reason(report.getReason())
            .status(report.getStatus())
            .createdAt(report.getCreatedAt())
            .processedAt(report.getProcessedAt())
            .processedBy(report.getProcessedBy() != null ?
                UserSimpleResponse.from(report.getProcessedBy()) : null)
            .build();
    }
}