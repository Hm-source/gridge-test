package org.example.gridgestagram.controller.file.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class BatchPresignedUrlRequest {

    @JsonProperty("files")
    @Valid
    @Size(min = 1, max = 10, message = "파일은 1개 이상 10개 이하로 요청해주세요")
    private List<PresignedUrlRequest> files;
}

