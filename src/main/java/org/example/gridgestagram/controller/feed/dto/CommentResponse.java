package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.feed.entity.Comment;

@Schema(description = "댓글 정보 응답")
@Getter
@Builder
public class CommentResponse {

    @Schema(description = "댓글 고유 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "좋은 사진이네요!")
    private String content;

    @Schema(description = "댓글 작성자 정보")
    private UserSimpleResponse user;

    @Schema(description = "댓글 작성 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 마지막 수정 시간", example = "2024-01-01 10:30:00")
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .user(UserSimpleResponse.from(comment.getUser()))
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
    }
}
