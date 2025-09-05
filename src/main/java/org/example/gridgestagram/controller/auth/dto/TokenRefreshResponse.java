package org.example.gridgestagram.controller.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenRefreshResponse {

    private String accessToken;
    private String tokenType;
    private LocalDateTime issuedAt;
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
