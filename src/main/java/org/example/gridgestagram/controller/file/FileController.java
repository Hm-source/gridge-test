package org.example.gridgestagram.controller.file;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.file.dto.BatchPresignedUrlRequest;
import org.example.gridgestagram.controller.file.dto.PresignedUrlRequest;
import org.example.gridgestagram.controller.file.dto.PresignedUrlResponse;
import org.example.gridgestagram.service.facade.S3Facade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Facade s3Facade;


    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
        @Valid @RequestBody PresignedUrlRequest request) {

        PresignedUrlResponse response = s3Facade.generatePresignedUploadUrl(
            request.getFileName(),
            request.getContentType()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/presigned-urls/batch")
    public ResponseEntity<List<PresignedUrlResponse>> generateBatchPresignedUrls(
        @Valid @RequestBody BatchPresignedUrlRequest request) {

        List<PresignedUrlResponse> responses = request.getFiles().stream()
            .map(fileRequest -> s3Facade.generatePresignedUploadUrl(
                fileRequest.getFileName(),
                fileRequest.getContentType()))
            .toList();

        return ResponseEntity.ok(responses);
    }
}
