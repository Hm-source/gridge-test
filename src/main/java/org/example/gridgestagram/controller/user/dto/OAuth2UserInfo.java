package org.example.gridgestagram.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "OAuth2 사용자 정보")
@Getter
@Builder
@AllArgsConstructor
public class OAuth2UserInfo {

    @Schema(description = "OAuth2 제공자 사용자 ID", example = "123456789")
    private String providerId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
}
