package org.example.gridgestagram.controller.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.term.entity.Terms;

@AllArgsConstructor
@Getter
@Builder
public class TermsResponse {

    private Long id;
    private String title;
    private String content;
    private Boolean isRequired;
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
