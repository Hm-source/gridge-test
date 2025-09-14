package org.example.gridgestagram.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;

@Schema(description = "사용자 정보 응답")
@AllArgsConstructor
@Getter
@Builder
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자명", example = "user123")
    private String username;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthdate;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "사용자 권한", example = "USER")
    private String role;

    @Schema(description = "구독 상태", example = "ACTIVE")
    private String subscriptionStatus;

    @Schema(description = "마지막 로그인 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime lastLoginAt;

    @Schema(description = "가입일시", example = "2024-01-01 10:00:00")
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