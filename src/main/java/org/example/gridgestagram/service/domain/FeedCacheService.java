package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.repository.feed.FeedRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final FeedRepository feedRepository;

    @Cacheable(value = "feed-exists", key = "#feedId")
    public boolean feedExists(Long feedId) {
        return feedRepository.existsById(feedId);
    }
}
