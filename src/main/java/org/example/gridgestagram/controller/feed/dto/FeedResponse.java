package org.example.gridgestagram.controller.feed.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.controller.user.dto.UserSimpleResponse;
import org.example.gridgestagram.repository.feed.entity.Feed;

@Getter
@Builder
public class FeedResponse {

    private Long id;
    private String content;
    private Boolean isVisible;
    private Integer likeCount;
    private Integer commentCount;
    private UserSimpleResponse user;
    private List<FileResponse> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FeedResponse from(Feed feed) {
        List<FileResponse> files = feed.getFiles().stream()
            .map(FileResponse::from)
            .sorted(Comparator.comparing(FileResponse::getOrder))
            .toList();

        return FeedResponse.builder()
            .id(feed.getId())
            .content(feed.getContent())
            .isVisible(feed.getIsVisible())
            .likeCount(feed.getLikeCount())
            .commentCount(feed.getCommentCount())
            .user(UserSimpleResponse.from(feed.getUser()))
            .files(files)
            .createdAt(feed.getCreatedAt())
            .updatedAt(feed.getUpdatedAt())
            .build();
    }
}
