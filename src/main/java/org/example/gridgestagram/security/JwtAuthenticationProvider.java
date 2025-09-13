package org.example.gridgestagram.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;
    private final UserService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
        String token = jwtToken.getToken();

        if (token == null || token.trim().isEmpty()) {
            throw new BadCredentialsException("JWT token is missing");
        }

        try {
            // 1. JWT 토큰 검증
            if (!jwtProvider.validateToken(token)) {
                throw new BadCredentialsException("Invalid JWT token");
            }

            // 2. JWT에서 사용자명 추출
            String username = jwtProvider.getUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) {
                throw new BadCredentialsException("JWT subject is missing");
            }

            // 3. UserDetails 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. 인증된 토큰 반환
            return new JwtAuthenticationToken(username, userDetails.getAuthorities());

        } catch (Exception e) {
            log.debug("JWT authentication failed: {}", e.getMessage());
            throw new BadCredentialsException("JWT authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.info("supports check: {}, input={}",
            JwtAuthenticationToken.class.isAssignableFrom(authentication),
            authentication);

        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
