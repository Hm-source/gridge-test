package org.example.gridgestagram.repository.log.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogType {
    USER_LOGIN("회원 로그인"),
    USER_LOGOUT("회원 로그아웃"),
    USER_SIGNUP("회원 가입"),
    USER_UPDATE("회원 정보 수정"),
    USER_DELETE("회원 탈퇴"),
    USER_PASSWORD_CHANGE("비밀번호 변경"),

    FEED_CREATE("피드 작성"),
    FEED_UPDATE("피드 수정"),
    FEED_DELETE("피드 삭제"),
    FEED_LIKE("피드 좋아요"),
    FEED_UNLIKE("피드 좋아요 취소"),
    FEED_VIEW("피드 조회"),
    FEED_REPORT("피드 신고"),

    COMMENT_CREATE("댓글 작성"),
    COMMENT_UPDATE("댓글 수정"),
    COMMENT_DELETE("댓글 삭제"),

    REPORT_CREATE("신고 접수"),
    REPORT_PROCESS("신고 처리"),

    ADMIN_USER_STATUS_CHANGE("관리자가 유저 상태 변경"),
    ADMIN_FEED_VIEW("관리자 피드 조회"),
    ADMIN_LOGIN("관리자 로그인"),
    ADMIN_FEED_DELETE("관리자가 피드 삭제"),
    ADMIN_USER_BAN("관리자 사용자 차단"),
    ADMIN_USER_VIEW("관리자 사용자 조회"),
    ADMIN_REPORT_HANDLE("관리자 신고 처리"),
    ADMIN_REPORT_VIEW("관리자 신고 조회");

    private final String description;

    public String getCategory() {
        if (name().startsWith("USER_")) {
            return "회원 관리";
        }
        if (name().startsWith("FEED_")) {
            return "피드 관리";
        }
        if (name().startsWith("COMMENT_")) {
            return "댓글 관리";
        }
        if (name().startsWith("REPORT_")) {
            return "신고 관리";
        }
        if (name().startsWith("ADMIN_")) {
            return "관리자";
        }
        return "기타";
    }
}