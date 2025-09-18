package org.example.gridgestagram.service.domain;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.security.JwtProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private static final String BLACKLIST_ACCESS_TOKEN_PREFIX = "blacklist:access:";
    private static final String BLACKLIST_REFRESH_TOKEN_PREFIX = "blacklist:refresh:";
    private static final String BLACKLIST_USER_PREFIX = "blacklist:user:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    public void blacklistAccessToken(String token, long expirationTime) {
        String key = BLACKLIST_ACCESS_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationTime, TimeUnit.SECONDS);
        log.info("Access token added to blacklist with expiration: {} seconds", expirationTime);
    }

    public void blacklistUserTokens(Long userId, int durationHours) {
        String key = BLACKLIST_USER_PREFIX + userId;
        long expirationTime = Duration.ofHours(durationHours).getSeconds();
        redisTemplate.opsForValue().set(key, "user_blacklisted", expirationTime, TimeUnit.SECONDS);
        log.warn("All tokens for user {} blacklisted for {} hours", userId, durationHours);
    }

    public boolean isAccessTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String key = BLACKLIST_ACCESS_TOKEN_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public boolean isUserTokensBlacklisted(Long userId) {
        String key = BLACKLIST_USER_PREFIX + userId;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public void blacklistTokenWithRemainingTime(String token) {
        try {
            if (!jwtProvider.validateToken(token)) {
                log.warn(
                    "Invalid token cannot be blacklisted: token is already expired or malformed");
                return;
            }

            long remainingTimeSeconds = getRemainingTokenTime(token);

            if (remainingTimeSeconds > 0) {
                blacklistAccessToken(token, remainingTimeSeconds);
            } else {
                log.info("Token already expired, no need to blacklist");
            }
        } catch (Exception e) {
            log.error("Error while blacklisting token: {}", e.getMessage());
        }
    }

    private long getRemainingTokenTime(String token) {
        try {
            Date expiration = jwtProvider.getExpiration(token);
            long expirationTime = expiration.getTime();
            long currentTime = System.currentTimeMillis();
            long remainingMillis = expirationTime - currentTime;
            if (remainingMillis <= 0) {
                return 0;
            }
            return remainingMillis / 1000;
        } catch (Exception e) {
            log.debug("Error calculating remaining token time: {}", e.getMessage());
            return 0;
        }
    }

    public void removeUserBlacklist(Long userId) {
        String key = BLACKLIST_USER_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("User {} removed from blacklist", userId);
    }
}