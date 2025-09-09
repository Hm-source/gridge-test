package org.example.gridgestagram.controller.feed.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadInfo {

    @NotBlank(message = "파일 URL은 필수입니다")
    @Size(max = 2048, message = "URL은 2048자를 초과할 수 없습니다")
    private String url;

    @NotNull(message = "파일 순서는 필수입니다")
    @Min(value = 0, message = "파일 순서는 0 이상이어야 합니다")
    private Integer order;
}