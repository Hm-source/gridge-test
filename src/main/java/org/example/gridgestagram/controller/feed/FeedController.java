package org.example.gridgestagram.controller.feed;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.feed.dto.FeedCreateRequest;
import org.example.gridgestagram.controller.feed.dto.FeedDetailResponse;
import org.example.gridgestagram.controller.feed.dto.FeedResponse;
import org.example.gridgestagram.controller.feed.dto.FeedUpdateRequest;
import org.example.gridgestagram.controller.feed.dto.ReportRequest;
import org.example.gridgestagram.controller.feed.dto.ReportResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.ReportService;
import org.example.gridgestagram.service.facade.FeedFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedFacade feedFacade;
    private final ReportService reportService;

    @PostMapping
    @LogAction(value = LogType.FEED_CREATE, targetType = "FEED")
    public ResponseEntity<FeedResponse> createFeed(
        @Valid @RequestBody FeedCreateRequest request) {
        FeedResponse response = feedFacade.createFeed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @LogAction(value = LogType.FEED_VIEW, targetType = "FEED")
    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeeds(
        @PageableDefault(size = 10)
        Pageable pageable) {
        Page<FeedResponse> response = feedFacade.getFeeds(pageable);
        return ResponseEntity.ok(response);
    }

    @LogAction(value = LogType.FEED_VIEW, targetType = "FEED")
    @GetMapping("/{feedId}")
    public ResponseEntity<FeedDetailResponse> getFeed(
        @PathVariable Long feedId) {
        FeedDetailResponse response = feedFacade.getFeed(feedId);
        return ResponseEntity.ok(response);
    }

    @LogAction(value = LogType.FEED_UPDATE, targetType = "FEED")
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponse> updateFeed(
        @PathVariable Long feedId,
        @Valid @RequestBody FeedUpdateRequest request) {

        FeedResponse response = feedFacade.updateFeed(feedId, request);
        return ResponseEntity.ok(response);
    }

    @LogAction(value = LogType.FEED_DELETE, targetType = "FEED")
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Long feedId) {
        feedFacade.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    @LogAction(value = LogType.REPORT_CREATE, targetType = "REPORT")
    @PostMapping("/{feedId}/reports")
    public ResponseEntity<ReportResponse> reportFeed(
        @PathVariable Long feedId,
        @Valid @RequestBody ReportRequest request) {

        ReportResponse response = reportService.report(request.getType(), feedId,
            request.getReason());
        return ResponseEntity.ok(response);
    }
}
