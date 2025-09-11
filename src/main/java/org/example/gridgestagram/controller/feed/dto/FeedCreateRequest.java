package org.example.gridgestagram.controller.feed.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedCreateRequest {

    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 1, max = 1000, message = "내용은 1자 이상 1000자 이하로 입력해주세요")
    private String content;

    @Valid
    @Size(max = 10, message = "파일은 최대 10개까지 업로드할 수 있습니다")
    private List<FileUploadInfo> files;
}
