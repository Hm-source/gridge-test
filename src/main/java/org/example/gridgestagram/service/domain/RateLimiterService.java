package org.example.gridgestagram.service.domain;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final String LIKE_TOGGLE_PREFIX = "like_toggle:";

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isAllowed(String identifier, String action, int maxRequests, Duration window) {
        String key = RATE_LIMIT_PREFIX + action + ":" + identifier;

        try {
            Object value = redisTemplate.opsForValue().get(key);

            int currentCount = 0;
            if (value != null) {
                try {
                    currentCount = Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    log.error("잘못된 값이 Redis에 저장됨 - key: {}, value: {}", key, value);
                    redisTemplate.delete(key); // 잘못된 값이면 초기화
                    currentCount = 0;
                }
            }

            if (currentCount >= maxRequests) {
                log.warn("Rate limit exceeded for identifier: {}, action: {}, current count: {}",
                    identifier, action, currentCount);
                return false;
            }

            if (currentCount == 0) {
                redisTemplate.opsForValue().set(key, 1, window.getSeconds(), TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().increment(key);
            }

            log.debug("Rate limit check passed for identifier: {}, action: {}, count: {}/{}",
                identifier, action, currentCount + 1, maxRequests);
            return true;

        } catch (Exception e) {
            log.error("Error checking rate limit for identifier: {}, action: {}", identifier,
                action, e);
            return true;
        }
    }

    public boolean isFeedLikeAllowed(Long userId, Long feedId) {
        String identifier = userId + ":" + feedId;
        return isAllowed(identifier, LIKE_TOGGLE_PREFIX + "feed", 3, Duration.ofSeconds(1));
    }

    public boolean isLikeToggleAllowed(Long userId) {
        return isAllowed(userId.toString(), LIKE_TOGGLE_PREFIX + "feed-global", 5,
            Duration.ofSeconds(3));
    }

}