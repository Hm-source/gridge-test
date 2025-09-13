package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.gridgestagram.repository.feed.entity.vo.FeedStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminFeedSearchCondition {

    private Long userId;
    private String username;
    private String name;
    private String phone;
    private String content;
    private FeedStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String orderBy = "createdAt";
    private String direction = "DESC";
}

