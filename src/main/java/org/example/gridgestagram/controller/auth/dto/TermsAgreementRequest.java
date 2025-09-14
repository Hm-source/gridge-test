package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "약관 동의 요청 정보")
@AllArgsConstructor
@Getter
@Builder
public class TermsAgreementRequest {

    @Schema(description = "약관별 동의 상태 목록")
    @NotNull(message = "약관 동의 정보는 필수입니다.")
    @Valid
    private List<TermsAgreementItem> agreements;

    @Schema(description = "개별 약관 동의 정보")
    @AllArgsConstructor
    @Getter
    @Builder
    public static class TermsAgreementItem {

        @Schema(description = "약관 고유 ID", example = "1")
        @NotNull(message = "약관 ID는 필수입니다.")
        private Long termsId;

        @Schema(description = "동의 여부 (true: 동의, false: 비동의)", example = "true")
        @NotNull(message = "동의 여부는 필수입니다.")
        private Boolean agreed;
    }
}
