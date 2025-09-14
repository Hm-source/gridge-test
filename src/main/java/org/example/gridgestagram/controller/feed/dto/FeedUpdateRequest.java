package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "피드 수정 요청 정보")
@Getter
@AllArgsConstructor
public class FeedUpdateRequest {

    @Schema(description = "수정할 피드 내용 (1-1000자)", example = "수정된 일상 사진입니다! #수정 #업데이트")
    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "내용은 1자 이상 1000자 이하로 입력해주세요")
    private String content;
}
