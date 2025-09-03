package org.example.gridgestagram.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationConfiguration authenticationConfiguration;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 인증 불필요한 경로는 JWT 검사 건너뛰기
        if (path.startsWith("/api/auth/") || path.startsWith("/api/public/")) {
            log.info("JwtAuthenticationFilter invoked for path: {}", path);

            filterChain.doFilter(request, response);
            return;
        }
        String token = resolveTokenFromHeader(request);
        log.info("Token: {}", token);

        if (StringUtils.hasText(token)) {
            try {
                // 1. 미인증 JwtAuthenticationToken 생성
                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token);

                // 2. AuthenticationManager에게 인증 위임
                AuthenticationManager authenticationManager =
                    authenticationConfiguration.getAuthenticationManager();
                log.info("authenticationManager: {}", authenticationManager.getClass());
                log.info("jwtAuthenticationToken class: {}", jwtAuthenticationToken.getClass());
                Authentication authentication = authenticationManager.authenticate(
                    jwtAuthenticationToken);

                // 3. SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("JWT authentication successful for user: {}", authentication.getName());

            } catch (AuthenticationException e) {
                log.info("JWT authentication failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}