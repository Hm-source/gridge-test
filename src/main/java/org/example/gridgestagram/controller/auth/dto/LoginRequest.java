package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Schema(description = "로그인 요청 정보")
@Getter
@Builder
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "로그인 아이디", example = "user123")
    @NotBlank(message = "아이디는은 필수입니다.")
    private String username;

    @Schema(description = "로그인 비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(this.username, this.password);
    }
}
