package org.example.gridgestagram.controller.feed.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;

@Getter
@AllArgsConstructor
public class ReportRequest {

    @NotNull(message = "신고 타입은 필수입니다.")  // FEED or COMMENT
    private ReportType type;

    @NotNull(message = "신고 사유는 필수입니다")
    private ReportReason reason;

    @Size(max = 200, message = "신고 상세 내용은 200자 이하로 입력해주세요")
    private String description;
}
