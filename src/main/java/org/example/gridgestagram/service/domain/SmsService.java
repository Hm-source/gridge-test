package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    // 실제 구현에서는 Twilio 를 사용한다고 가정
    public void sendPasswordResetCode(String phone, String code) {
        try {
            String message = String.format(
                "[GridgestaGram] 비밀번호 초기화 인증번호: %s (1시간 유효)",
                code
            );

            log.info("SMS 발송 완료 - phone: {}, code: {}", phone, code);
        } catch (Exception e) {
            log.error("SMS 발송 실패", e);
            throw new RuntimeException("SMS 발송 실패");
        }
    }
}
