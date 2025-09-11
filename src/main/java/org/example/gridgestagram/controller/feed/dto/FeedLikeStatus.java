package org.example.gridgestagram.controller.feed.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedLikeStatus {

    private Long feedId;
    private Boolean liked;
    private Integer likeCount;
}
