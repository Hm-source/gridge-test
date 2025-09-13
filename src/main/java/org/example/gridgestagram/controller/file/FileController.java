package org.example.gridgestagram.controller.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "파일 업로드", description = "S3 파일 업로드를 위한 Presigned URL 생성 관련 API")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Facade s3Facade;

    @Operation(
        summary = "Presigned URL 생성 (단일 파일)",
        description = "단일 파일 업로드를 위한 S3 Presigned URL을 생성합니다. 클라이언트는 반환된 presignedUrl을 사용하여 직접 S3에 파일을 업로드할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Presigned URL 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PresignedUrlResponse.class),
                examples = @ExampleObject(
                    value = "{\"presignedUrl\": \"https://s3.amazonaws.com/bucket/uploads/image.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256...\", \"finalUrl\": \"https://s3.amazonaws.com/bucket/uploads/image.jpg\", \"fileName\": \"image.jpg\", \"objectKey\": \"uploads/image.jpg\", \"expiresIn\": 3600}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (파일명 또는 Content-Type 누락)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"파일명은 필수입니다.\", \"timestamp\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (로그인 필요)",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
        @Valid @RequestBody PresignedUrlRequest request) {

        PresignedUrlResponse response = s3Facade.generatePresignedUploadUrl(
            request.getFileName(),
            request.getContentType()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Presigned URL 일괄 생성 (다중 파일)",
        description = "다중 파일 업로드를 위한 S3 Presigned URL들을 일괄 생성합니다. 최대 10개의 파일을 동시에 요청할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Presigned URL 일괄 생성 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "[{\"presignedUrl\": \"https://s3.amazonaws.com/bucket/uploads/image1.jpg?X-Amz-Algorithm=...\", \"finalUrl\": \"https://s3.amazonaws.com/bucket/uploads/image1.jpg\", \"fileName\": \"image1.jpg\", \"objectKey\": \"uploads/image1.jpg\", \"expiresIn\": 3600}, {\"presignedUrl\": \"https://s3.amazonaws.com/bucket/uploads/image2.jpg?X-Amz-Algorithm=...\", \"finalUrl\": \"https://s3.amazonaws.com/bucket/uploads/image2.jpg\", \"fileName\": \"image2.jpg\", \"objectKey\": \"uploads/image2.jpg\", \"expiresIn\": 3600}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (파일 개수 초과 또는 유효성 검증 실패)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"파일은 1개 이상 10개 이하로 요청해주세요.\", \"timestamp\": \"2024-01-01 10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (로그인 필요)",
            content = @Content(mediaType = "application/json")
        )
    })
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
