package org.example.gridgestagram.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.service.domain.TokenBlacklistService;
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
    private final TokenBlacklistService tokenBlacklistService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = resolveTokenFromHeader(request);
        log.info("Token: {}", token);

        if (StringUtils.hasText(token)) {
            try {
                if (tokenBlacklistService.isAccessTokenBlacklisted(token)) {
                    log.warn("Access token is blacklisted, rejecting request");
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }
                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token);

                AuthenticationManager authenticationManager =
                    authenticationConfiguration.getAuthenticationManager();
                Authentication authentication = authenticationManager.authenticate(
                    jwtAuthenticationToken);

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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
            || path.startsWith("/api/auth/")
            || path.startsWith("/api-docs")
            || path.equals("/swagger-ui.html")
            || path.startsWith("/api/public/");
    }
}