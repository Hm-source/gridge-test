package org.example.gridgestagram.service.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.admin.dto.PrivacyConsentRenewalTarget;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.term.TermsRepository;
import org.example.gridgestagram.repository.term.UserTermsRepository;
import org.example.gridgestagram.repository.term.entity.Terms;
import org.example.gridgestagram.repository.term.entity.UserTerms;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenewalNotificationService {

    private final UserTermsRepository userTermsRepository;
    private final TermsRepository termsRepository;

    @Transactional
    public List<PrivacyConsentRenewalTarget> getPrivacyConsentRenewalTargets(int daysBeforeExpiry) {
        LocalDate targetDate = LocalDate.now().plusDays(daysBeforeExpiry);

        Terms privacyPolicyTerms = termsRepository.findByTitleContaining("데이터")
            .orElseThrow(() -> new CustomException(ErrorCode.TERMS_NOT_FOUND));

        List<UserTerms> expiringUserTerms = userTermsRepository
            .findByTermsIdAndNextAgreedDateAndIsAgreed(
                privacyPolicyTerms.getId(),
                targetDate,
                true
            );
        return expiringUserTerms.stream()
            .map(userTerms -> {
                User user = userTerms.getUser();
                return PrivacyConsentRenewalTarget.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .phone(user.getPhone())
                    .daysUntilExpiry(
                        ChronoUnit.DAYS.between(LocalDate.now(), userTerms.getNextAgreedDate()))
                    .build();
            })
            .toList();
    }

    @Transactional
    public void sendPrivacyConsentRenewalNotifications(int daysBeforeExpiry) {
        List<PrivacyConsentRenewalTarget> targets = getPrivacyConsentRenewalTargets(
            daysBeforeExpiry);

        log.info("개인정보 처리동의 갱신 알림 대상자: {}명 ({}일 전)", targets.size(), daysBeforeExpiry);

        for (PrivacyConsentRenewalTarget target : targets) {
            try {
                log.info("개인정보 처리동의 갱신 알림 발송 완료: {}", target.getUsername());

            } catch (Exception e) {
                log.error("개인정보 처리동의 갱신 알림 발송 실패 - 사용자: {}, 오류: {}",
                    target.getUsername(), e.getMessage());
            }
        }
    }
}
