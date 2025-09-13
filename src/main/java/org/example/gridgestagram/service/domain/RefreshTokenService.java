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

    public void store(Long userId, String refreshToken) {
        refreshTokenRepository.save(userId, refreshToken);
        log.info("Refresh token stored for user: {}", userId);
    }

    public boolean validate(Long userId, String refreshToken) {
        return refreshTokenRepository.findByUserId(userId)
            .map(storedToken -> storedToken.equals(refreshToken))
            .orElse(false);
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("Refresh token deleted for user: {}", userId);
    }
}