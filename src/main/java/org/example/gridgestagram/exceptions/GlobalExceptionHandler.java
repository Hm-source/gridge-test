package org.example.gridgestagram.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        CustomException ex, HttpServletRequest request) {

        log.warn("Business exception [{}]: {}", ex.getErrorCode().getCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.from(ex.getErrorCode(), request.getRequestURI());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed: {}", ex.getMessage());

        // 검증 실패한 필드들의 메시지만 추출
        List<String> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.from(
            ErrorCode.VALIDATION_FAILED,
            request.getRequestURI(),
            details
        );

        return ResponseEntity.badRequest().body(error);
    }

    // Spring Security 예외들
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
        BadCredentialsException ex, HttpServletRequest request) {

        return handleBusinessException(
            new CustomException(ErrorCode.INVALID_CREDENTIALS),
            request
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
        UsernameNotFoundException ex, HttpServletRequest request) {

        return handleBusinessException(
            new CustomException(ErrorCode.USER_NOT_FOUND),
            request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest request) {

        return handleBusinessException(
            new CustomException(ErrorCode.ACCESS_DENIED),
            request
        );
    }

    // JWT 예외들
    @ExceptionHandler({JwtException.class, ExpiredJwtException.class})
    public ResponseEntity<ErrorResponse> handleJwtException(
        Exception ex, HttpServletRequest request) {

        ErrorCode errorCode = ex instanceof ExpiredJwtException
            ? ErrorCode.TOKEN_EXPIRED
            : ErrorCode.INVALID_CREDENTIALS;

        return handleBusinessException(
            new CustomException(errorCode),
            request
        );
    }

    // OAuth2 예외
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleOAuth2Exception(
        OAuth2AuthenticationException ex, HttpServletRequest request) {

        return handleBusinessException(
            new CustomException(ErrorCode.OAUTH2_FAILED),
            request
        );
    }

    // 데이터베이스 예외
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
        DataIntegrityViolationException ex, HttpServletRequest request) {

        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;

        // 간단한 중복 감지
        if (ex.getMessage().contains("username")) {
            errorCode = ErrorCode.DUPLICATE_USERNAME;
        } else if (ex.getMessage().contains("phone")) {
            errorCode = ErrorCode.DUPLICATE_PHONE;
        }

        return handleBusinessException(
            new CustomException(errorCode),
            request
        );
    }

    // 일반 예외들
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
        IllegalArgumentException ex, HttpServletRequest request) {

        return handleBusinessException(
            new CustomException(ErrorCode.VALIDATION_FAILED, ex.getMessage()),
            request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
        Exception ex, HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        return handleBusinessException(
            new CustomException(ErrorCode.INTERNAL_ERROR),
            request
        );
    }
}
