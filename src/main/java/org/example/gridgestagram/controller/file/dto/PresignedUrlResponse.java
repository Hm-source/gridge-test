package org.example.gridgestagram.controller.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Presigned URL 생성 결과 응답")
@Getter
@Builder
public class PresignedUrlResponse {

    @Schema(description = "S3 직접 업로드를 위한 Presigned URL", example = "https://s3.amazonaws.com/bucket/uploads/image.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256...")
    private String presignedUrl;  // S3 업로드용 URL

    @Schema(description = "업로드 완료 후 공개적으로 접근 가능한 최종 URL", example = "https://s3.amazonaws.com/bucket/uploads/image.jpg")
    private String finalUrl;      // 게시물에 저장될 최종 URL

    @Schema(description = "업로드된 파일명", example = "profile_image.jpg")
    private String fileName;

    @Schema(description = "S3 객체 키 (전체 경로)", example = "uploads/2024/01/01/profile_image.jpg")
    private String objectKey;

    @Schema(description = "Presigned URL 만료 시간 (초 단위)", example = "3600")
    private long expiresIn;
}

