package org.example.gridgestagram.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;

@Schema(description = "사용자 간략 정보 응답")
@AllArgsConstructor
@Getter
@Builder
public class UserSimpleResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자명", example = "user123")
    private String username;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "사용자 권한", example = "USER")
    private String role;

    @Schema(description = "구독 상태", example = "ACTIVE")
    private String subscriptionStatus;

    public static UserSimpleResponse from(User user) {
        return UserSimpleResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .profileImageUrl(user.getProfileImageUrl())
            .role(user.getRole().name())
            .subscriptionStatus(user.getSubscriptionStatus().name())
            .build();
    }
}
