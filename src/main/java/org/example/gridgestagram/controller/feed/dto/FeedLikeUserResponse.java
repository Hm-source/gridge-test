package org.example.gridgestagram.controller.feed.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedLikeUserResponse {

    private Long userId;
    private String username;
    private String profileImageUrl;
    private LocalDateTime likedAt;
}
