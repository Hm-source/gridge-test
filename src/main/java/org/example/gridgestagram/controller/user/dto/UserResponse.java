package org.example.gridgestagram.controller.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;

@AllArgsConstructor
@Getter
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private LocalDate birthdate;
    private String profileImageUrl;
    private String role;
    private String subscriptionStatus;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .birthdate(user.getBirthdate())
            .profileImageUrl(user.getProfileImageUrl())
            .role(user.getRole().name())
            .subscriptionStatus(user.getSubscriptionStatus().name())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .build();
    }
}