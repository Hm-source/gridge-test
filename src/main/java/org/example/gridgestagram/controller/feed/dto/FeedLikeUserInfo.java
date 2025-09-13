package org.example.gridgestagram.controller.feed.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedLikeUserInfo {

    private Long userId;
    private Long likedTimestamp; // milliseconds

    public LocalDateTime getLikedAt() {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(likedTimestamp),
            ZoneId.systemDefault()
        );
    }
}
