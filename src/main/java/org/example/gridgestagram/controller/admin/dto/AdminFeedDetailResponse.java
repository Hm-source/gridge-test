package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.feed.entity.vo.FeedStatus;
import org.example.gridgestagram.repository.files.entity.Files;

@Schema(description = "관리자용 피드 상세 정보 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFeedDetailResponse {

    @Schema(description = "피드 ID", example = "1")
    private Long id;

    @Schema(description = "작성자 사용자 ID", example = "123")
    private Long userId;

    @Schema(description = "작성자 사용자명", example = "user123")
    private String username;

    @Schema(description = "작성자 전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "작성자 실명", example = "홍길동")
    private String name;

    @Schema(description = "피드 내용", example = "오늘 날씨가 좋네요!")
    private String content;

    @Schema(description = "첨부 이미지 URL 목록")
    private List<String> imageUrls;

    @Schema(description = "피드 상태", example = "ACTIVE")
    private FeedStatus status;

    @Schema(description = "좋아요 수", example = "15")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "8")
    private Integer commentCount;

    @Schema(description = "신고 수", example = "2")
    private Integer reportCount;

    @Schema(description = "생성일시", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-01 10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시", example = "2024-01-01 11:00:00")
    private LocalDateTime deletedAt;

    @Schema(description = "삭제자 정보", example = "admin")
    private String deletedBy;

    public static AdminFeedDetailResponse from(Feed feed) {
        List<String> imageUrls = feed.getFiles().stream()
            .map(Files::getUrl)
            .collect(Collectors.toList());

        return AdminFeedDetailResponse.builder()
            .id(feed.getId())
            .userId(feed.getUser().getId())
            .username(feed.getUser().getUsername())
            .phone(feed.getUser().getPhone())
            .name(feed.getUser().getName())
            .content(feed.getContent())
            .imageUrls(imageUrls)
            .status(feed.getStatus())
            .likeCount(feed.getLikeCount())
            .commentCount(feed.getCommentCount())
            .reportCount(feed.getReportCount())
            .createdAt(feed.getCreatedAt())
            .updatedAt(feed.getUpdatedAt())
            .deletedAt(feed.getDeletedAt())
            .deletedBy(feed.getDeletedBy())
            .build();
    }
}
