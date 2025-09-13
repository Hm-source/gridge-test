package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "토큰 갱신 요청 정보")
@Getter
@Builder
@AllArgsConstructor
public class TokenRefreshRequest {

    @Schema(description = "갱신용 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

    public static TokenRefreshRequest from(String refreshToken) {
        return TokenRefreshRequest.builder()
            .refreshToken(refreshToken)
            .build();
    }
}
