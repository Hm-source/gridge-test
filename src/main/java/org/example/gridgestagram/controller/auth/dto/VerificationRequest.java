package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "인증 코드 확인 요청 정보")
@Getter
@AllArgsConstructor
public class VerificationRequest {

    @Schema(description = "비밀번호 재설정 요청 시 발급받은 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "토큰은 필수입니다")
    private String token;

    @Schema(description = "SMS로 받은 6자리 인증 코드", example = "123456")
    @NotBlank(message = "인증 코드는 필수입니다")
    @Size(min = 6, max = 6, message = "인증 코드는 6자리입니다")
    private String verificationCode;
}
