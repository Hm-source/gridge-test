package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "댓글 작성 요청 정보")
@Getter
@AllArgsConstructor
public class CommentCreateRequest {

    @Schema(description = "댓글 내용 (1-1000자)", example = "좋은 사진이네요! 어디서 찍으셨나요?")
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "댓글은 1자 이상 1000자 이하로 입력해주세요")
    private String content;
}