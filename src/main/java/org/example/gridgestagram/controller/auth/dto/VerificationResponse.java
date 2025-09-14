package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "인증 코드 확인 결과 응답")
@Getter
@AllArgsConstructor
@Builder
public class VerificationResponse {

    @Schema(description = "인증 성공 여부 (true: 성공, false: 실패)", example = "true")
    private boolean verified;

    @Schema(description = "인증 결과 메시지", example = "인증이 완료되었습니다.")
    private String message;
}
