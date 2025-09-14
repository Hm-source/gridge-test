package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "계정 관리 작업 결과 응답")
@Getter
@Builder
public class AccountActionResponse {

    @Schema(description = "작업 성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "작업 결과 메시지", example = "계정이 일시정지되었습니다.")
    private String message;

    @Schema(description = "수행된 작업 유형", example = "SUSPEND")
    private String action;

    @Schema(description = "작업 수행 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime timestamp;
}