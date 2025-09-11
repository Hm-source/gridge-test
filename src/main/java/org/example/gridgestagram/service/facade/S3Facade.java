package org.example.gridgestagram.service.facade;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.file.dto.PresignedUrlResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.files.entity.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Facade {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public PresignedUrlResponse generatePresignedUploadUrl(String fileName, String contentType) {
        validateFile(fileName, contentType);

        try {
            String objectKey = generateObjectKey(fileName);

            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 15;
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);

            generatePresignedUrlRequest.putCustomRequestHeader("Content-Type", contentType);

            URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            String finalUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, "ap-northeast-2", objectKey);

            return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl.toString())
                .finalUrl(finalUrl) // 게시물 생성 시 사용할 URL
                .fileName(fileName)
                .objectKey(objectKey)
                .expiresIn(900) // 15분
                .build();

        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    public boolean verifyFileExists(String finalUrl) {
        try {
            String objectKey = extractObjectKeyFromUrl(finalUrl);
            return amazonS3.doesObjectExist(bucketName, objectKey);
        } catch (Exception e) {
            log.error("파일 존재 확인 실패: {}", e.getMessage());
            return false;
        }
    }

    private String generateObjectKey(String fileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        return String.format("feeds/%s_%s%s", timestamp, uuid, extension);
    }

    private void validateFile(String fileName, String contentType) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        String lowerFileName = fileName.toLowerCase();

        boolean isValidExtension = Arrays.stream(allowedExtensions)
            .anyMatch(lowerFileName::endsWith);

        if (!isValidExtension) {
            throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        List<String> allowedContentTypes = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/jpg"
        );

        if (!allowedContentTypes.contains(contentType)) {
            throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        }
    }

    private String extractObjectKeyFromUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            return uri.getPath().substring(1);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }
    }

    @Async
    public void deleteFilesFromS3Async(Feed feed) {
        List<String> fileUrls = feed.getFiles().stream()
            .map(Files::getUrl)
            .toList();
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String objectKey = extractObjectKeyFromUrl(fileUrl);

            if (amazonS3.doesObjectExist(bucketName, objectKey)) {
                amazonS3.deleteObject(bucketName, objectKey);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.S3_DELETED_FAILED);
        }
    }
}
