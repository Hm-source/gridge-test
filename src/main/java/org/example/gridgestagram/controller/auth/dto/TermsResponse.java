package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.term.entity.Terms;

@Schema(description = "약관 정보 응답")
@AllArgsConstructor
@Getter
@Builder
public class TermsResponse {

    @Schema(description = "약관 고유 ID", example = "1")
    private Long id;

    @Schema(description = "약관 제목", example = "서비스 이용약관")
    private String title;

    @Schema(description = "약관 내용", example = "본 약관은 그리드게스타그램 서비스 이용에 관한 사항을 정합니다...")
    private String content;

    @Schema(description = "필수 동의 여부 (true: 필수, false: 선택)", example = "true")
    private Boolean isRequired;

    @Schema(description = "약관 생성 일시", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    public static TermsResponse from(Terms terms) {
        return TermsResponse.builder()
            .id(terms.getId())
            .title(terms.getTitle())
            .content(terms.getContent())
            .isRequired(terms.getIsRequired())
            .createdAt(terms.getCreatedAt())
            .build();
    }
}
