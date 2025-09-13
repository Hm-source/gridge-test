package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.subscription.entity.vo.SubscriptionStatus;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.Provider;
import org.example.gridgestagram.repository.user.entity.vo.Role;
import org.example.gridgestagram.repository.user.entity.vo.UserStatus;

@Getter
@Builder
public class UserDetailResponse {

    private Long id;
    private String username;
    private String name;
    private String phone;
    private UserStatus status;
    private String statusDescription;
    private Provider provider;
    private Role role;
    private SubscriptionStatus subscriptionStatus;
    private LocalDate birthdate;
    private LocalDateTime lastLoginAt;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long daysSinceLastLogin;

    public static UserDetailResponse from(User user) {
        Long daysSinceLastLogin = null;
        if (user.getLastLoginAt() != null) {
            daysSinceLastLogin = ChronoUnit.DAYS.between(
                user.getLastLoginAt().toLocalDate(),
                LocalDate.now()
            );
        }

        return UserDetailResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .phone(user.getPhone())
            .status(user.getStatus())
            .statusDescription(getStatusDescription(user.getStatus()))
            .provider(user.getProvider())
            .role(user.getRole())
            .subscriptionStatus(user.getSubscriptionStatus())
            .birthdate(user.getBirthdate())
            .lastLoginAt(user.getLastLoginAt())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .daysSinceLastLogin(daysSinceLastLogin)
            .build();
    }

    private static String getStatusDescription(UserStatus status) {
        return switch (status) {
            case ACTIVE -> "활성";
            case INACTIVE -> "비활성";
            case DORMANT -> "휴면";
            case SUSPENDED -> "일시정지";
            case WITHDRAWN -> "탈퇴";
        };
    }
}
