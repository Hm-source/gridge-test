package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "개인정보 동의 갱신 대상 사용자 정보")
@Getter
@Builder
public class PrivacyConsentRenewalTarget {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자명", example = "user123")
    private String username;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "개인정보 동의 만료일시", example = "2024-12-31 23:59:59")
    private LocalDateTime privacyConsentExpiresAt;

    @Schema(description = "만료까지 남은 일수", example = "30")
    private Long daysUntilExpiry;
}
