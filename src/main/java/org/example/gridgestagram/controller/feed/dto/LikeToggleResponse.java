package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "좋아요 토글 결과 응답")
@Getter
@Builder
public class LikeToggleResponse {

    @Schema(description = "현재 좋아요 상태 (true: 좋아요 누름, false: 좋아요 취소)", example = "true")
    private Boolean liked;

    @Schema(description = "피드의 전체 좋아요 수", example = "15")
    private Integer likeCount;

    @Schema(description = "좋아요 토글 결과 메시지", example = "좋아요를 추가했습니다.")
    private String message;

    @Schema(description = "좋아요 토글 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime timestamp;
}
