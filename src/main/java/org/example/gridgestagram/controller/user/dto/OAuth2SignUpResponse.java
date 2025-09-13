package org.example.gridgestagram.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2SignUpResponse {

    private Long userId;
    private String username;
    private String name;
    private String provider;
    private String accessToken;
    private String refreshToken;
}
