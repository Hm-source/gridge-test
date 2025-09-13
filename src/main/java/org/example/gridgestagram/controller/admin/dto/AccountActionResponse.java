package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountActionResponse {

    private Boolean success;
    private String message;
    private String action;
    private LocalDateTime timestamp;
}