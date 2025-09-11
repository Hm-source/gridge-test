package org.example.gridgestagram.repository.feed.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedStatus {
    ACTIVE("활성"),
    USER_DELETED("사용자 삭제"),
    ADMIN_DELETED("관리자 삭제"),
    REPORTED("신고됨"),
    HIDDEN("숨김");

    private final String description;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == USER_DELETED || this == ADMIN_DELETED;
    }
}