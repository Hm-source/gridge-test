package org.example.gridgestagram.controller.file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlRequest {

    @NotBlank(message = "파일명은 필수입니다")
    @Size(max = 255, message = "파일명은 255자를 초과할 수 없습니다")
    private String fileName;

    @NotBlank(message = "Content-Type은 필수입니다")
    private String contentType;
}
