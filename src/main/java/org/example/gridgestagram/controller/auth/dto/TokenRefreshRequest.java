package org.example.gridgestagram.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenRefreshRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

    public static TokenRefreshRequest from(String refreshToken) {
        return TokenRefreshRequest.builder()
            .refreshToken(refreshToken)
            .build();
    }
}
