package org.example.gridgestagram.controller.feed.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeToggleResponse {

    private Boolean liked;
    private Integer likeCount;
    private String message;
    private LocalDateTime timestamp;
}
