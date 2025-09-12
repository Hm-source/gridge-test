package org.example.gridgestagram.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.admin.dto.AdminFeedDetailResponse;
import org.example.gridgestagram.controller.admin.dto.AdminFeedResponse;
import org.example.gridgestagram.controller.admin.dto.AdminFeedSearchCondition;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.AdminFeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/feeds")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
public class AdminFeedController {

    private final AdminFeedService adminFeedService;

    @LogAction(value = LogType.ADMIN_FEED_VIEW, targetType = "FEED")
    @GetMapping
    public ResponseEntity<Page<AdminFeedResponse>> searchFeeds(
        @ModelAttribute AdminFeedSearchCondition condition,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdminFeedResponse> result = adminFeedService.searchFeeds(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @LogAction(value = LogType.ADMIN_FEED_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}")
    public ResponseEntity<AdminFeedDetailResponse> getFeedDetail(@PathVariable Long feedId) {
        AdminFeedDetailResponse response = adminFeedService.getFeedDetail(feedId);
        return ResponseEntity.ok(response);
    }

    @LogAction(value = LogType.ADMIN_FEED_DELETE, targetType = "FEED")
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
        @PathVariable Long feedId,
        @RequestParam(required = false) String reason) {
        adminFeedService.deleteFeed(feedId, reason);
        return ResponseEntity.noContent().build();
    }
}
