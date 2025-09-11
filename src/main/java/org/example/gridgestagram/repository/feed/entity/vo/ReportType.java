package org.example.gridgestagram.repository.feed.entity.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    FEED("피드"),
    COMMENT("댓글");

    private final String description;
}
