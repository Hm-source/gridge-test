package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "피드 좋아요 상태 정보")
@Getter
@Builder
public class FeedLikeStatus {

    @Schema(description = "피드 고유 ID", example = "1")
    private Long feedId;

    @Schema(description = "현재 사용자의 좋아요 여부 (true: 좋아요 누름, false: 좋아요 안누름)", example = "true")
    private Boolean liked;

    @Schema(description = "피드의 전체 좋아요 수", example = "25")
    private Integer likeCount;
}
