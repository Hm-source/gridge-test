package org.example.gridgestagram.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.auth.dto.NewPasswordRequest;
import org.example.gridgestagram.controller.auth.dto.PasswordResetRequest;
import org.example.gridgestagram.controller.auth.dto.VerificationRequest;
import org.example.gridgestagram.controller.auth.dto.VerificationResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @LogAction(value = LogType.USER_PASSWORD_CHANGE_REQUEST, targetType = "USER")
    @PostMapping("/reset/request")
    public ResponseEntity<String> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    @LogAction(value = LogType.USER_VERIFY, targetType = "USER")
    @PostMapping("/reset/verify")
    public ResponseEntity<VerificationResponse> verifyCode(
        @Valid @RequestBody VerificationRequest request) {
        VerificationResponse response = passwordResetService.verifyCode(
            request.getToken(),
            request.getVerificationCode()
        );
        return ResponseEntity.ok(response);
    }

    @LogAction(value = LogType.USER_PASSWORD_CHANGE, targetType = "USER")
    @PostMapping("/reset/complete")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @LogAction(value = LogType.USER_VERIFY_RESEND, targetType = "USER")
    @PostMapping("/reset/resend")
    public ResponseEntity<String> resendVerificationCode(@RequestParam String token) {
        passwordResetService.resendVerificationCode(token);
        return ResponseEntity.ok("인증 코드가 재전송되었습니다.");
    }
}