package org.example.gridgestagram.controller.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.user.entity.User;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserResponse user;
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
