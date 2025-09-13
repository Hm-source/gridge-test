package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.feed.entity.Feed;

@Getter
@Builder
public class AdminFeedResponse {

    private Long id;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
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
