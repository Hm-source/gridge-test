package org.example.gridgestagram.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.service.domain.RenewalNotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalNotificationScheduler {

    private final RenewalNotificationService renewalNotificationService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPrivacyConsent1DayNotice() {
        log.info("개인정보 처리동의 1일 전 알림 발송 시작");
        renewalNotificationService.sendPrivacyConsentRenewalNotifications(1);
        log.info("개인정보 처리동의 1일 전 알림 발송 완료");
    }

}
