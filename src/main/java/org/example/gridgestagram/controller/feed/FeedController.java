package org.example.gridgestagram.controller.feed;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.feed.dto.FeedCreateRequest;
import org.example.gridgestagram.controller.feed.dto.FeedResponse;
import org.example.gridgestagram.service.facade.FeedFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedFacade feedFacade;

    @PostMapping
    public ResponseEntity<FeedResponse> createFeed(
        @Valid @RequestBody FeedCreateRequest request) {
        FeedResponse response = feedFacade.createFeed(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getFeeds(
        @PageableDefault(size = 10)
        Pageable pageable) {
        Page<FeedResponse> response = feedFacade.getFeeds(pageable);
        return ResponseEntity.ok(response);
    }

}
