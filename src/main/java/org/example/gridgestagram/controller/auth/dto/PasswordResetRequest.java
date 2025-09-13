package org.example.gridgestagram.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.vo.VerificationMethod;

@Getter
@AllArgsConstructor
public class PasswordResetRequest {

    @NotBlank(message = "전화번호는 필수입니다")
    private String identifier;

    @NotNull(message = "인증 방법은 필수입니다")
    private VerificationMethod verificationMethod;
}
