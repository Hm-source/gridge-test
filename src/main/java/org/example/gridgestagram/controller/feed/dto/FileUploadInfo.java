package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "파일 업로드 정보")
@Getter
@AllArgsConstructor
public class FileUploadInfo {

    @Schema(description = "업로드된 파일의 URL 주소", example = "https://s3.amazonaws.com/bucket/images/photo1.jpg")
    @NotBlank(message = "파일 URL은 필수입니다")
    @Size(max = 2048, message = "URL은 2048자를 초과할 수 없습니다")
    private String url;

    @Schema(description = "파일 표시 순서 (0부터 시작)", example = "0")
    @NotNull(message = "파일 순서는 필수입니다")
    @Min(value = 0, message = "파일 순서는 0 이상이어야 합니다")
    private Integer order;
}