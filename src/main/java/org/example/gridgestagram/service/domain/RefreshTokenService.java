package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.repository.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public void store(Long userId, String refreshToken) {
        refreshTokenRepository.save(userId, refreshToken);
        log.info("Refresh token stored for user: {}", userId);
    }

    public boolean validate(Long userId, String refreshToken) {
        if (tokenBlacklistService.isRefreshTokenBlacklisted(refreshToken)) {
            log.warn("Refresh token is blacklisted for user: {}", userId);
            return false;
        }
        if (tokenBlacklistService.isUserTokensBlacklisted(userId)) {
            log.warn("All tokens are blacklisted for user: {}", userId);
            return false;
        }
        return refreshTokenRepository.findByUserId(userId)
            .map(storedToken -> storedToken.equals(refreshToken))
            .orElse(false);
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.findByUserId(userId).ifPresent(token -> {
            tokenBlacklistService.blacklistTokenWithRemainingTime(token, true);
        });
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh token deleted and blacklisted for user: {}", userId);
    }

    public void blacklistAllUserTokens(Long userId, int durationHours) {
        tokenBlacklistService.blacklistUserTokens(userId, durationHours);
        refreshTokenRepository.deleteByUserId(userId);
        log.warn("All tokens blacklisted for user: {} for {} hours", userId, durationHours);
    }
}