package org.example.gridgestagram.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "비밀번호 재설정", description = "비밀번호 찾기 및 재설정 관련 API")
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(
        summary = "비밀번호 재설정 요청",
        description = "전화번호를 통해 비밀번호 재설정을 요청합니다. SMS로 6자리 인증코드가 전송됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "인증 코드 전송 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "\"인증 코드가 전송되었습니다.\""
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (전화번호 오류 등)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "등록되지 않은 전화번호",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.USER_PASSWORD_CHANGE_REQUEST, targetType = "USER")
    @PostMapping("/reset/request")
    public ResponseEntity<String> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    @Operation(
        summary = "인증 코드 확인",
        description = "SMS로 받은 6자리 인증 코드를 확인합니다. 인증 성공 시 비밀번호 변경을 위한 토큰을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "인증 코드 확인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VerificationResponse.class),
                examples = @ExampleObject(
                    value = "{\"verified\": true, \"message\": \"인증이 완료되었습니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 인증 코드",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"verified\": false, \"message\": \"인증 코드가 올바르지 않습니다.\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "만료된 또는 유효하지 않은 토큰",
            content = @Content(mediaType = "application/json")
        )
    })
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

    @Operation(
        summary = "비밀번호 재설정 완료",
        description = "인증 후 발급받은 토큰을 사용하여 새로운 비밀번호로 변경합니다. 비밀번호는 6-20자이며 확인 비밀번호와 일치해야 합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "비밀번호 변경 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "\"비밀번호가 성공적으로 변경되었습니다.\""
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (비밀번호 불일치, 유효성 검증 실패)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 토큰",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.USER_PASSWORD_CHANGE, targetType = "USER")
    @PostMapping("/reset/complete")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @Operation(
        summary = "인증 코드 재전송",
        description = "인증 코드를 다시 전송합니다. 기존 코드는 무효화되고 새로운 6자리 코드가 SMS로 전송됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "인증 코드 재전송 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "\"인증 코드가 재전송되었습니다.\""
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (토큰 오류)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 토큰",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.USER_VERIFY_RESEND, targetType = "USER")
    @PostMapping("/reset/resend")
    public ResponseEntity<String> resendVerificationCode(
        @Parameter(description = "비밀번호 재설정 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
        @RequestParam String token) {
        passwordResetService.resendVerificationCode(token);
        return ResponseEntity.ok("인증 코드가 재전송되었습니다.");
    }
}