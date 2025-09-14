package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "토큰 갱신 성공 응답 정보")
@Getter
@Builder
@AllArgsConstructor
public class TokenRefreshResponse {

    @Schema(description = "새로 발급된 JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "토큰 발급 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime issuedAt;

    @Schema(description = "토큰 만료까지 남은 시간(초)", example = "86400")
    private Long expiresIn;

    public static TokenRefreshResponse from(String accessToken, Long expiresIn) {
        return TokenRefreshResponse.builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .issuedAt(LocalDateTime.now())
            .expiresIn(expiresIn)
            .build();
    }

}
