package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "신고 처리 요청")
@Getter
@AllArgsConstructor
public class ReportProcessRequest {

    @Schema(description = "신고 승인 여부 (true: 승인, false: 반려)", example = "true")
    @NotNull(message = "승인/거부 여부는 필수입니다")
    private boolean approve;

    @Schema(description = "신고 처리 사유 (200자 이하)", example = "부적절한 콘텐츠로 인한 승인")
    @Size(max = 200, message = "처리 사유는 200자 이하로 입력해주세요")
    private String processReason;
}
