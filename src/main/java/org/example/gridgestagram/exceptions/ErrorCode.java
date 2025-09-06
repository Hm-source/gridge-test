package org.example.gridgestagram.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 인증/권한
    INVALID_CREDENTIALS("ERROR_AUTH_001", "로그인 정보가 올바르지 않습니다.", 401),
    TOKEN_EXPIRED("ERROR_AUTH_002", "로그인이 만료되었습니다.", 401),
    ACCESS_DENIED("ERROR_AUTH_003", "접근 권한이 없습니다.", 403),

    // 사용자
    USER_NOT_FOUND("ERROR_USER_001", "사용자를 찾을 수 없습니다.", 404),
    DUPLICATE_USERNAME("ERROR_USER_002", "이미 사용 중인 아이디입니다.", 409),
    DUPLICATE_PHONE("ERROR_USER_003", "이미 사용 중인 전화번호입니다.", 409),

    // 약관
    TERMS_AGREEMENT_REQUIRED("ERROR_TERMS_001", "필수 약관에 동의해주세요.", 400),
    TERMS_NOT_FOUND("ERROR_TERMS_002", "약관을 찾을 수 없습니다.", 404),

    // OAuth2
    OAUTH2_FAILED("ERROR_OAUTH_001", "소셜 로그인에 실패했습니다.", 401),

    // 입력값 검증
    VALIDATION_FAILED("ERROR_VALIDATION_001", "입력값을 확인해주세요.", 400),

    // 시스템
    INTERNAL_ERROR("ERROR_SYSTEM_001", "서버 오류가 발생했습니다.", 500);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}