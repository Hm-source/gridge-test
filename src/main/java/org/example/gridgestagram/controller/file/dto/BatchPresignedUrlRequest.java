package org.example.gridgestagram.controller.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Presigned URL 일괄 생성 요청 정보")
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class BatchPresignedUrlRequest {

    @Schema(description = "업로드할 파일들의 정보 목록 (1-10개)")
    @JsonProperty("files")
    @Valid
    @Size(min = 1, max = 10, message = "파일은 1개 이상 10개 이하로 요청해주세요")
    private List<PresignedUrlRequest> files;
}

