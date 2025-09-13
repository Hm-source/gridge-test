package org.example.gridgestagram.controller.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class TermsAgreementRequest {

    @NotNull(message = "약관 동의 정보는 필수입니다.")
    @Valid
    private List<TermsAgreementItem> agreements;

    @AllArgsConstructor
    @Getter
    @Builder
    public static class TermsAgreementItem {

        @NotNull(message = "약관 ID는 필수입니다.")
        private Long termsId;

        @NotNull(message = "동의 여부는 필수입니다.")
        private Boolean agreed;
    }
}
