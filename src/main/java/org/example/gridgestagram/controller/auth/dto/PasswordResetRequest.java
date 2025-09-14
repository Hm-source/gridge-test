package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.vo.VerificationMethod;

@Schema(description = "비밀번호 재설정 요청 정보")
@Getter
@AllArgsConstructor
public class PasswordResetRequest {

    @Schema(description = "비밀번호 재설정을 위한 전화번호", example = "01012345678")
    @NotBlank(message = "전화번호는 필수입니다")
    private String identifier;

    @Schema(description = "인증 방법 (SMS 등)", example = "SMS")
    @NotNull(message = "인증 방법은 필수입니다")
    private VerificationMethod verificationMethod;
}
