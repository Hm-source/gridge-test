package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "새 비밀번호 설정 요청 정보")
@Getter
@AllArgsConstructor
public class NewPasswordRequest {

    @Schema(description = "인증 완료 후 발급받은 비밀번호 재설정 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    @NotBlank(message = "토큰은 필수입니다")
    private String token;

    @Schema(description = "새로운 비밀번호 (6-20자)", example = "newPassword123")
    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 6, max = 20, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String newPassword;

    @Schema(description = "비밀번호 확인 (새 비밀번호와 동일해야 함)", example = "newPassword123")
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다")
    public boolean isPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
