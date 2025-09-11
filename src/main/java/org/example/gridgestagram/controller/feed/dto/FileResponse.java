package org.example.gridgestagram.controller.feed.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.files.entity.Files;

@Getter
@AllArgsConstructor
@Builder
public class FileResponse {

    private Long id;
    private String url;
    private Integer order;
    private LocalDateTime createdAt;
    
    public static FileResponse from(Files file) {
        return FileResponse.builder()
            .id(file.getId())
            .url(file.getUrl())
            .order(file.getOrder())
            .createdAt(file.getCreatedAt())
            .build();
    }
}
