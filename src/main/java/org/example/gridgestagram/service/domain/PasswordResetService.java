package org.example.gridgestagram.service.domain;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.auth.dto.PasswordResetRequest;
import org.example.gridgestagram.controller.auth.dto.VerificationResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.user.PasswordResetTokenRepository;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.PasswordResetToken;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.VerificationMethod;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    // Redis 키 패턴
    private static final String RESET_ATTEMPT_KEY = "password_reset_attempt:";
    private static final String VERIFY_ATTEMPT_KEY = "verify_attempt:";
    // 제한 설정
    private static final int MAX_RESET_ATTEMPTS = 5; // 1시간당 최대 5회
    private static final int MAX_VERIFY_ATTEMPTS = 5; // 10분당 최대 5회
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SmsService smsService;

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        checkResetAttemptLimit(request.getIdentifier());

        User user = findUserByIdentifier(request.getIdentifier());

        invalidateExistingTokens(user);

        String token = generateSecureToken();
        String verificationCode = generateVerificationCode();

        PasswordResetToken resetToken = PasswordResetToken.create(
            user, token, verificationCode, request.getVerificationMethod()
        );

        tokenRepository.save(resetToken);

        sendVerificationCode(user, verificationCode, request.getVerificationMethod());
        incrementResetAttempt(request.getIdentifier());

        log.info("비밀번호 초기화 요청 - userId: {}, method: {}",
            user.getId(), request.getVerificationMethod());
    }


    @Transactional
    public VerificationResponse verifyCode(String token, String verificationCode) {
        checkVerifyAttemptLimit(token);

        PasswordResetToken resetToken = tokenRepository.findByTokenAndIsUsedFalse(token)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESET_TOKEN));

        if (resetToken.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_RESET_TOKEN);
        }

        if (!resetToken.getVerificationCode().equals(verificationCode)) {
            incrementVerifyAttempt(token);
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        resetToken.verify();

        log.info("인증 코드 검증 완료 - userId: {}, token: {}",
            resetToken.getUser().getId(), token);

        return VerificationResponse.builder()
            .verified(true)
            .message("인증이 완료되었습니다. 새 비밀번호를 설정하세요.")
            .build();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenAndIsUsedFalse(token)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESET_TOKEN));

        if (!resetToken.isValid()) {
            throw new CustomException(ErrorCode.INVALID_RESET_TOKEN);
        }

        User user = resetToken.getUser();

        validateNewPassword(newPassword, user);

        String encodedPassword = passwordEncoder.encode(newPassword);
        updateUserPassword(user, encodedPassword);

        resetToken.use();

        invalidateExistingTokens(user);

        log.info("비밀번호 초기화 완료 - userId: {}", user.getId());
    }


    @Transactional
    public void resendVerificationCode(String token) {
        checkResetAttemptLimit(token);

        PasswordResetToken resetToken = tokenRepository.findByTokenAndIsUsedFalse(token)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESET_TOKEN));

        if (resetToken.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_RESET_TOKEN);
        }

        if (resetToken.isVerified()) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED_TOKEN);
        }

        String newVerificationCode = generateVerificationCode();
        updateVerificationCode(resetToken, newVerificationCode);

        sendVerificationCode(resetToken.getUser(), newVerificationCode,
            resetToken.getVerificationMethod());

        incrementResetAttempt(token);

        log.info("인증 코드 재전송 - userId: {}, method: {}",
            resetToken.getUser().getId(), resetToken.getVerificationMethod());
    }

    private User findUserByIdentifier(String phone) {
        Optional<User> userOpt = userRepository.findByPhone(phone);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    private void invalidateExistingTokens(User user) {
        List<PasswordResetToken> existingTokens = tokenRepository.findByUserAndIsUsedFalse(user);
        for (PasswordResetToken token : existingTokens) {
            token.use();
        }
    }

    private String generateSecureToken() {
        return UUID.randomUUID().toString().replace("-", "") +
            UUID.randomUUID().toString().replace("-", "");
    }

    private String generateVerificationCode() {
        Random random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자
        return String.valueOf(code);
    }

    private void sendVerificationCode(User user, String code, VerificationMethod method) {
        try {
            if (method == VerificationMethod.SMS) {
                smsService.sendPasswordResetCode(user.getPhone(), code);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.VERIFICATION_SEND_FAILED);
        }
    }

    private void validateNewPassword(String password, User user) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (user.getPassword() == null) {
            log.warn("카카오 로그인 유저이기 떄문에 id/password 기반 회원가입을 진행해야합니다.");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.SAME_AS_CURRENT_PASSWORD);
        }
        if (!isValidPasswordFormat(password)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }

    private boolean isValidPasswordFormat(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,20}$");
    }

    private void updateUserPassword(User user, String encodedPassword) {
        try {
            user.updatePassword(encodedPassword);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("비밀번호 업데이트 실패", e);
            throw new CustomException(ErrorCode.PASSWORD_UPDATE_FAILED);
        }
    }

    private void updateVerificationCode(PasswordResetToken token, String newCode) {
        try {
            Field codeField = PasswordResetToken.class.getDeclaredField("verificationCode");
            codeField.setAccessible(true);
            codeField.set(token, newCode);
            tokenRepository.save(token);
        } catch (Exception e) {
            log.error("인증 코드 업데이트 실패", e);
            throw new CustomException(ErrorCode.VERIFICATION_CODE_UPDATE_FAILED);
        }
    }


    private void checkResetAttemptLimit(String identifier) {
        String key = RESET_ATTEMPT_KEY + identifier;
        Object attempts = redisTemplate.opsForValue().get(key);

        if (attempts != null && ((Number) attempts).intValue() >= MAX_RESET_ATTEMPTS) {
            throw new CustomException(ErrorCode.TOO_MANY_RESET_ATTEMPTS);
        }
    }

    private void incrementResetAttempt(String identifier) {
        String key = RESET_ATTEMPT_KEY + identifier;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofHours(1));
    }

    private void checkVerifyAttemptLimit(String token) {
        String key = VERIFY_ATTEMPT_KEY + token;
        Object attempts = redisTemplate.opsForValue().get(key);

        if (attempts != null && ((Number) attempts).intValue() >= MAX_VERIFY_ATTEMPTS) {
            throw new CustomException(ErrorCode.TOO_MANY_VERIFY_ATTEMPTS);
        }
    }

    private void incrementVerifyAttempt(String token) {
        String key = VERIFY_ATTEMPT_KEY + token;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(10));
    }
}