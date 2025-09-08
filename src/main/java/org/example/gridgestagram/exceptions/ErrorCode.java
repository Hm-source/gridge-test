package org.example.gridgestagram.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 인증/권한
    UNAUTHORIZED("AUTH_001", "인증이 필요합니다.", 401),
    INVALID_AUTHENTICATION("AUTH_002", "유효하지 않은 인증 정보입니다.", 401),
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
    INTERNAL_ERROR("ERROR_SYSTEM_001", "서버 오류가 발생했습니다.", 500),

    // 파일 관련
    TOO_MANY_FILES("FILE_001", "파일은 최대 10개까지 업로드할 수 있습니다.", 400),
    INVALID_FILE_ORDER("FILE_002", "파일 순서가 유효하지 않습니다.", 400),
    DUPLICATE_FILE_ORDER("FILE_003", "중복된 파일 순서입니다.", 400),
    FILE_SAVE_FAILED("FILE_004", "파일 저장에 실패하였습니다.", 500),
    INVALID_FILE("FILE_005", "파일이 유효하지 않습니다..", 400),

    // 게시물 관련
    FEED_LIST_FETCH_FAILED("FEED_001", "피드 목록을 불러오는데 실패하였습니다.", 500),
    INVALID_FEED_ID("FEED_VALIDATION_001", "피드 ID가 유효하지 않습니다.", 400),
    FEED_CREATE_FAILED("FEED_002", "피드 생성을 실패하였습니다.", 500),
    FEED_UPDATE_FAILED("FEED_003", "피드 업데이트를 실패하였습니다.", 500),
    FEED_DELETE_FAILED("FEED_004", "피드 삭제를 실패하였습니다.", 500),
    FEED_FETCH_FAILED("FEED_005", "게시물 조회에 실패했습니다.", 500),
    FEED_NOT_FOUND("FEED_006", "피드를 찾을 수 없습니다.", 500),

    // 공통
    INVALID_REQUEST("COMMON_001", "유효하지 않은 요청입니다.", 400);
    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}