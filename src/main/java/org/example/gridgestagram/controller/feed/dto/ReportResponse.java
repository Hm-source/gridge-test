package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.feed.entity.Report;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;
import org.example.gridgestagram.repository.user.entity.User;

@Schema(description = "신고 접수 결과 응답")
@Getter
@Builder
public class ReportResponse {

    @Schema(description = "신고 고유 ID", example = "1")
    private Long id;

    @Schema(description = "신고 타입", example = "FEED")
    private ReportType type;

    @Schema(description = "신고 대상 ID (피드 ID 또는 댓글 ID)", example = "1")
    private Long targetId;

    @Schema(description = "신고된 콘텐츠 내용", example = "신고된 피드 내용")
    private String content;

    @Schema(description = "신고자 정보")
    private UserSimpleResponse reporter;

    @Schema(description = "신고된 콘텐츠 작성자 정보")
    private UserSimpleResponse writer;

    @Schema(description = "신고 사유", example = "INAPPROPRIATE_CONTENT")
    private ReportReason reason;

    @Schema(description = "신고 처리 상태 (PENDING: 대기중, APPROVED: 승인)", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "신고 접수 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "신고 처리 시간", example = "2024-01-01 15:00:00", nullable = true)
    private LocalDateTime processedAt;

    @Schema(description = "신고 처리자 정보", nullable = true)
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