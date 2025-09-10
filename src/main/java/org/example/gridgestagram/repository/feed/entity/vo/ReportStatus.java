package org.example.gridgestagram.repository.feed.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING("대기중"),
    APPROVED("승인됨");

    private final String description;

}
