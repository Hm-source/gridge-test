package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.gridgestagram.repository.feed.entity.vo.FeedStatus;

@Schema(description = "관리자용 피드 검색 조건")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminFeedSearchCondition {

    @Schema(description = "작성자 사용자 ID 필터", example = "123")
    private Long userId;

    @Schema(description = "작성자 사용자명 검색", example = "user123")
    private String username;

    @Schema(description = "작성자 실명 검색", example = "홍길동")
    private String name;

    @Schema(description = "작성자 전화번호 검색", example = "01012345678")
    private String phone;

    @Schema(description = "피드 내용 검색", example = "날씨")
    private String content;

    @Schema(description = "피드 상태 필터", example = "ACTIVE")
    private FeedStatus status;

    @Schema(description = "검색 시작일시", example = "2024-01-01 00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "검색 종료일시", example = "2024-12-31 23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "정렬 기준 필드", example = "createdAt")
    private String orderBy = "createdAt";

    @Schema(description = "정렬 방향", example = "DESC")
    private String direction = "DESC";
}

