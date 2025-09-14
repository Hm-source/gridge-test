package org.example.gridgestagram.controller.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.feed.entity.vo.FeedStatus;

@Schema(description = "피드 정보 응답")
@Getter
@Builder(toBuilder = true)
public class FeedResponse {

    @Schema(description = "피드 고유 ID", example = "1")
    private Long id;

    @Schema(description = "피드 내용", example = "오늘의 일상을 공유합니다!")
    private String content;

    @Schema(description = "피드 상태 (ACTIVE: 활성, HIDDEN: 숨김 처리)", example = "ACTIVE")
    private FeedStatus status;

    @Schema(description = "좋아요 수", example = "15")
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "8")
    private Integer commentCount;

    @Schema(description = "피드 작성자 정보")
    private UserSimpleResponse user;

    @Schema(description = "첨부된 파일 목록")
    private List<FileResponse> files;

    @Schema(description = "최근 댓글 목록 (최대 3개)")
    private List<CommentResponse> recentComments;

    @Schema(description = "피드 작성 시간", example = "2024-01-01 10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "피드 마지막 수정 시간", example = "2024-01-01 10:30:00")
    private LocalDateTime updatedAt;

    public static FeedResponse from(Feed feed) {
        List<FileResponse> files = feed.getFiles().stream()
            .map(FileResponse::from)
            .sorted(Comparator.comparing(FileResponse::getOrder))
            .toList();

        return FeedResponse.builder()
            .id(feed.getId())
            .content(feed.getContent())
            .status(feed.getStatus())
            .likeCount(feed.getLikeCount())
            .commentCount(feed.getCommentCount())
            .user(UserSimpleResponse.from(feed.getUser()))
            .files(files)
            .createdAt(feed.getCreatedAt())
            .updatedAt(feed.getUpdatedAt())
            .build();
    }

    public static FeedResponse fromWithComments(Feed feed, List<CommentResponse> recentComments) {
        FeedResponse response = from(feed);
        return response.toBuilder()
            .recentComments(recentComments != null ? recentComments : Collections.emptyList())
            .build();
    }
}
