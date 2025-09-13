package org.example.gridgestagram.controller.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class VerificationResponse {

    private boolean verified;
    private String message;
}
