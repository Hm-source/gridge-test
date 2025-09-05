package org.example.gridgestagram.controller.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.auth.dto.TermsAgreementRequest;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2SignUpRequest {

    @NotBlank(message = "카카오 정보는 필수입니다.")
    private String kakaoInfo; // Base64 인코딩된 카카오 정보

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식을 입력해주세요.")
    private String phone;

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

    @Valid
    @NotNull(message = "약관 동의 정보는 필수입니다.")
    private TermsAgreementRequest termsAgreement;

    public static class OAuth2SignUpResponse {

    }
}
