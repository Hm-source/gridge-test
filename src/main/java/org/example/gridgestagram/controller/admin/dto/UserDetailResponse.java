package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "관리자용 사용자 상세 정보 응답")
@Getter
@Builder
public class UserDetailResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 아이디", example = "user123")
    private String username;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "사용자 상태", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "사용자 상태 설명", example = "활성")
    private String statusDescription;

    @Schema(description = "가입 경로", example = "LOCAL")
    private Provider provider;

    @Schema(description = "사용자 권한", example = "USER")
    private Role role;

    @Schema(description = "구독 상태", example = "ACTIVE")
    private SubscriptionStatus subscriptionStatus;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthdate;

    @Schema(description = "마지막 로그인 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime lastLoginAt;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "가입일시", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-01 10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "마지막 로그인 이후 경과 일수", example = "5")
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
