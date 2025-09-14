package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "피드 작성 요청 정보")
@Getter
@AllArgsConstructor
public class FeedCreateRequest {

    @Schema(description = "피드 내용 텍스트 (1-1000자)", example = "오늘의 일상을 공유합니다! #일상 #행복")
    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "내용은 1자 이상 1000자 이하로 입력해주세요")
    private String content;

    @Schema(description = "첨부할 이미지/비디오 파일 목록 (최대 10개)")
    @Valid
    @Size(max = 10, message = "파일은 최대 10개까지 업로드할 수 있습니다")
    private List<FileUploadInfo> files;
}
