package org.example.gridgestagram.controller.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Presigned URL 생성 요청 정보")
@Getter
@AllArgsConstructor
public class PresignedUrlRequest {

    @Schema(description = "업로드할 파일명 (확장자 포함, 255자 이하)", example = "profile_image.jpg")
    @NotBlank(message = "파일명은 필수입니다")
    @Size(max = 255, message = "파일명은 255자를 초과할 수 없습니다")
    private String fileName;

    @Schema(description = "파일 MIME 타입 (image/jpeg, image/png, video/mp4 등)", example = "image/jpeg")
    @NotBlank(message = "Content-Type은 필수입니다")
    private String contentType;
}
