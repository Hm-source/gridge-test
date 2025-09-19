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
    INVALID_FILE("FILE_005", "파일이 유효하지 않습니다.", 400),
    FILE_NOT_UPLOADED_TO_S3("FILE_06", "파일이 S3에 업로드되지 않았습니다.", 400),
    INVALID_FILE_EXTENSION("FILE_07", "지원하지 않는 파일 확장자입니다.", 400),
    INVALID_CONTENT_TYPE("FILE_08", "지원하지 않는 Content-Type입니다.", 400),
    S3_UPLOAD_FAILED("FILE_09", "S3에 업로드하는데 실패하였습니다.", 500),
    S3_DELETED_FAILED("FILE_10", "S3 파일 삭제를 실패하여습니다.", 500),
    // 게시물 관련
    FEED_LIST_FETCH_FAILED("FEED_001", "피드 목록을 불러오는데 실패하였습니다.", 500),
    INVALID_FEED_ID("FEED_VALIDATION_001", "피드 ID가 유효하지 않습니다.", 400),
    FEED_CREATE_FAILED("FEED_002", "피드 생성을 실패하였습니다.", 500),
    FEED_UPDATE_FAILED("FEED_003", "피드 업데이트를 실패하였습니다.", 500),
    FEED_DELETE_FAILED("FEED_004", "피드 삭제를 실패하였습니다.", 500),
    FEED_FETCH_FAILED("FEED_005", "게시물 조회에 실패했습니다.", 500),
    FEED_NOT_FOUND("FEED_006", "피드를 찾을 수 없습니다.", 404),
    FEED_HIDE_FAILED("FEED_007", "피드 숨김 처리를 실패하였습니다.", 500),
    ALREADY_LIKED("FEED_008", "이미 좋아요를 누른 피드입니다.", 400),
    LIKE_NOT_FOUND("FEED_009", "좋아요를 찾을 수 없습니다.", 404),
    LIKE_REDIS_TRANSACTION_FAILED("FEED_010", "Redis 트랜잭션 처리에 실패했습니다.", 500),

    // 신고 관련
    CANNOT_REPORT_OWN_FEED("REPORT_001", "본인 피드 또는 댓글은 신고할 수 없습니다.", 400),
    FEED_REPORT_FAILED("REPORT_002", "피드 또는 댓글 신고를 실패하였습니다.", 500),
    ALREADY_REPORTED("REPORT_003", "이미 신고한 피드 또는 댓글입니다.", 400),
    REPORT_NOT_FOUND("REPORT_004", "신고 ID에 해당하는 신고 건을 찾을 수 없습니다.", 404),

    // 댓글 관련
    COMMENT_NOT_FOUND("COMMENT_001", "댓글을 찾을 수 없습니다.", 404),
    COMMENT_ACCESS_DENIED("COMMENT_002", "댓글에 대한 권한이 없습니다.", 403),
    COMMENT_CREATE_FAILED("COMMENT_003", "댓글 생성에 실패했습니다.", 500),
    COMMENT_UPDATE_FAILED("COMMENT_004", "댓글 수정에 실패했습니다.", 500),
    COMMENT_DELETE_FAILED("COMMENT_005", "댓글 삭제에 실패했습니다.", 500),
    COMMENT_LIST_FETCH_FAILED("COMMENT_006", "댓글 목록 조회에 실패했습니다.", 500),

    // 비밀번호 관련
    INVALID_RESET_TOKEN("RESET_001", "유효하지 않은 초기화 토큰입니다.", 400),
    EXPIRED_RESET_TOKEN("RESET_002", "만료된 초기화 토큰입니다.", 400),
    INVALID_VERIFICATION_CODE("RESET_003", "잘못된 인증 코드입니다.", 400),
    TOO_MANY_RESET_ATTEMPTS("RESET_004", "너무 많은 초기화 시도입니다. 1시간 후 다시 시도하세요.", 429),
    TOO_MANY_VERIFY_ATTEMPTS("RESET_005", "너무 많은 인증 시도입니다. 10분 후 다시 시도하세요.", 429),
    VERIFICATION_SEND_FAILED("RESET_006", "인증 코드 전송에 실패했습니다.", 500),
    INVALID_PASSWORD_FORMAT("RESET_007", "비밀번호는 6자~20자, 영문+숫자 조합이어야 합니다.", 400),
    SAME_AS_CURRENT_PASSWORD("RESET_008", "현재 비밀번호와 동일합니다.", 400),
    PASSWORD_UPDATE_FAILED("RESET_009", "비밀번호 변경에 실패했습니다.", 500),
    ALREADY_VERIFIED_TOKEN("RESET_010", "이미 인증된 토큰입니다.", 400),
    VERIFICATION_CODE_UPDATE_FAILED("RESET_011", "인증 코드 업데이트에 실패했습니다.", 500),

    ACCOUNT_ALREADY_SUSPENDED("ACCOUNT_001", "이미 일시정지된 계정입니다.", 400),
    ACCOUNT_ALREADY_WITHDRAWN("ACCOUNT_002", "이미 탈퇴한 계정입니다.", 400),
    ACCOUNT_NOT_SUSPENDED("ACCOUNT_003", "일시정지되지 않은 계정입니다.", 400),
    ACCOUNT_NOT_DORMANT("ACCOUNT_004", "휴면 계정이 아닙니다.", 400),


    USER_NOT_ACTIVE("USER_001", "활성화된 회원이 아닙니다.", 500),

    ADMIN_ACCESS_DENIED("ADMIN_001", "관리자 권한이 필요합니다", 403),
    ADMIN_FEED_SEARCH_FAILED("ADMIN_002", "관리자 피드 검색에 실패했습니다", 500),
    ADMIN_FEED_DETAIL_FAILED("ADMIN_003", "관리자 피드 상세 조회에 실패했습니다", 500),
    ADMIN_FEED_DELETE_FAILED("ADMIN_004", "관리자 피드 삭제에 실패했습니다", 500),


    LOG_SEARCH_FAILED("LOG_001", "로그 조회에 실패하였습니다.", 500),
    RECENT_LOGS_FAILED("LOG_002", "최신 로그를 조회하는데 실패하였습니다.", 500),
    USER_LOGS_FAILED("LOG_002", "사용자 로그를 조회하는데 실패하였습니다.", 500),

    // 공통
    INVALID_REQUEST("COMMON_001", "유효하지 않은 요청입니다.", 400),
    TOKEN_BLACKLISTED("COMMON_002", "차단된 토큰입니다.", 401),
    USER_TOKENS_BLACKLISTED("COMMON_003", "사용자의 모든 토큰이 무효화되었습니다. 다시 로그인해주세요.", 401),
    INVALID_REFRESH_TOKEN("COMMON_004", "유효하지 않은 리프레시 토큰입니다.", 401),

    // Rate Limiting
    TOO_MANY_REQUESTS("RATE_001", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", 429);
    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}