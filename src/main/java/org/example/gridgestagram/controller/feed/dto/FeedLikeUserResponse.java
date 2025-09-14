package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "피드 좋아요 누른 사용자 정보")
@Getter
@Builder
public class FeedLikeUserResponse {

    @Schema(description = "사용자 고유 ID", example = "2")
    private Long userId;

    @Schema(description = "사용자 아이디", example = "user123")
    private String username;

    @Schema(description = "사용자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "좋아요 누른 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime likedAt;
}
