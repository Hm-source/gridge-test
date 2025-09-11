package org.example.gridgestagram.controller.admin.dto;

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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFeedDetailResponse {

    private Long id;
    private Long userId;
    private String username;
    private String phone;
    private String name;
    private String content;
    private List<String> imageUrls;
    private FeedStatus status;
    private Integer likeCount;
    private Integer commentCount;
    private Integer reportCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
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
