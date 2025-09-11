package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrivacyConsentRenewalTarget {

    private Long userId;
    private String username;
    private String phone;
    private LocalDateTime privacyConsentExpiresAt;
    private Long daysUntilExpiry;
}
