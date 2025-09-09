package org.example.gridgestagram.controller.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlResponse {

    private String presignedUrl;  // S3 업로드용 URL
    private String finalUrl;      // 게시물에 저장될 최종 URL
    private String fileName;
    private String objectKey;
    private long expiresIn;
}

