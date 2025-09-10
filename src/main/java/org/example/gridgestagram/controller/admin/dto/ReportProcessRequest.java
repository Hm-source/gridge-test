package org.example.gridgestagram.controller.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportProcessRequest {

    @NotNull(message = "승인/거부 여부는 필수입니다")
    private boolean approve; // true: 승인(게시물 숨김)

    @Size(max = 200, message = "처리 사유는 200자 이하로 입력해주세요")
    private String processReason;
}
