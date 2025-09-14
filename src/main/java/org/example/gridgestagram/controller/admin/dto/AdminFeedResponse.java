package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.feed.entity.Feed;

@Schema(description = "관리자용 피드 목록 응답")
@Getter
@Builder
public class AdminFeedResponse {

    @Schema(description = "피드 ID", example = "1")
    private Long id;

    @Schema(description = "작성자 사용자 ID", example = "123")
    private Long userId;

    @Schema(description = "작성자 사용자명", example = "user123")
    private String username;

    @Schema(description = "피드 내용", example = "오늘 날씨가 좋네요!")
    private String content;

    @Schema(description = "생성일시", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-01 10:30:00")
    private LocalDateTime updatedAt;

    public static AdminFeedResponse from(Feed feed) {

        return AdminFeedResponse.builder()
            .id(feed.getId())
            .userId(feed.getUser().getId())
            .username(feed.getUser().getUsername())
            .content(feed.getContent())
            .createdAt(feed.getCreatedAt())
            .updatedAt(feed.getUpdatedAt())
            .build();
    }
}
