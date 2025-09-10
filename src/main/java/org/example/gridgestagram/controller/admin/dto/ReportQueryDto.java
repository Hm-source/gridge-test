package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;

@Getter
@Builder
@AllArgsConstructor
public class ReportQueryDto {

    private Long reportId;
    private ReportType type;
    private Long targetId;
    private String content; // Feed.content or Comment.content
    private String reporterName;
    private String writerName;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}