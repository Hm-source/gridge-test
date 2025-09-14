package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.user.entity.User;

@Schema(description = "로그인 성공 응답 정보")
@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "로그인한 사용자 정보")
    private UserResponse user;

    @Schema(description = "로그인 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime loginAt;

    public static LoginResponse from(String accessToken, String refreshToken, User user) {
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .user(UserResponse.from(user))
            .loginAt(LocalDateTime.now())
            .build();
    }
}
