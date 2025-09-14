package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;

@Schema(description = "신고 요청 정보")
@Getter
@AllArgsConstructor
public class ReportRequest {

    @Schema(description = "신고 대상 타입 (FEED: 피드, COMMENT: 댓글)", example = "FEED")
    @NotNull(message = "신고 타입은 필수입니다.")  // FEED or COMMENT
    @NotNull(message = "신고 타입은 필수입니다.")
    private ReportType type;

    @Schema(description = "신고 사유 (부적절한 내용, 스팸, 저작권 침해 등)", example = "INAPPROPRIATE_CONTENT")
    @NotNull(message = "신고 사유는 필수입니다")
    private ReportReason reason;
}
