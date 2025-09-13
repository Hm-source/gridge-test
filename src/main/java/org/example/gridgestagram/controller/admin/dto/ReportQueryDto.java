package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;

@Schema(description = "관리자용 신고 목록 조회 응답")
@Getter
@Builder
@AllArgsConstructor
public class ReportQueryDto {

    @Schema(description = "신고 ID", example = "1")
    private Long reportId;

    @Schema(description = "신고 대상 유형", example = "FEED")
    private ReportType type;

    @Schema(description = "신고 대상 ID", example = "123")
    private Long targetId;

    @Schema(description = "신고된 콘텐츠 내용", example = "오늘 날씨가 좋네요!")
    private String content;

    @Schema(description = "신고자 이름", example = "reporter123")
    private String reporterName;

    @Schema(description = "작성자 이름", example = "writer123")
    private String writerName;

    @Schema(description = "신고 사유", example = "INAPPROPRIATE_CONTENT")
    private ReportReason reason;

    @Schema(description = "신고 처리 상태", example = "PENDING")
    private ReportStatus status;

    @Schema(description = "신고 접수 일시", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "신고 처리 일시", example = "2024-01-01 11:00:00")
    private LocalDateTime processedAt;
}