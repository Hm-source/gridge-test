package org.example.gridgestagram.repository.feed;

import org.example.gridgestagram.controller.admin.dto.AdminFeedSearchCondition;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminFeedRepositoryCustom {

    Page<Feed> searchFeeds(AdminFeedSearchCondition condition, Pageable pageable);
}
