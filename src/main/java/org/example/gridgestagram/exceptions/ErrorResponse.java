package org.example.gridgestagram.exceptions;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private List<String> details; // 상세 에러 목록 (선택)

    public static ErrorResponse from(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .status(errorCode.getStatus())
            .path(path)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static ErrorResponse from(ErrorCode errorCode, String path, List<String> details) {
        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .status(errorCode.getStatus())
            .path(path)
            .timestamp(LocalDateTime.now())
            .details(details)
            .build();
    }
}
