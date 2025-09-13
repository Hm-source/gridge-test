package org.example.gridgestagram.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "OAuth2 (카카오) 회원가입 요청")
@Getter
@Builder
@AllArgsConstructor
public class OAuth2SignUpRequest {

    @Schema(description = "Base64 인코딩된 카카오 사용자 정보", example = "eyJpZCI6MTIzLCJuYW1lIjoi홍길돕...")
    @NotBlank(message = "카카오 정보는 필수입니다.")
    private String kakaoInfo;

    @Schema(description = "전화번호 (01012345678 형식)", example = "01012345678")
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식을 입력해주세요.")
    private String phone;

    @Schema(description = "생년월일 (과거 날짜)", example = "1990-01-01")
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

    @Schema(description = "약관 동의 정보")
    @Valid
    @NotNull(message = "약관 동의 정보는 필수입니다.")
    private TermsAgreementRequest termsAgreement;

    public static class OAuth2SignUpResponse {

    }
}
