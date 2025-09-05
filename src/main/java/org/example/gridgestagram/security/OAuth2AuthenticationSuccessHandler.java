package org.example.gridgestagram.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.RefreshTokenService;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        try {
            // loadUser()에서 설정한 isNewUser 플래그 확인
            Boolean isNewUser = (Boolean) oAuth2User.getAttribute("isNewUser");

            if (isNewUser != null && isNewUser) {
                log.info("신규 사용자 - 회원가입 페이지로 리다이렉트");
                handleNewUser(oAuth2User, request, response);
            } else {
                log.info("기존 사용자 - 로그인 처리");
                handleExistingUser(oAuth2User, request, response);
            }
        } catch (Exception e) {
            log.error("OAuth2 authentication success handling failed", e);
            redirectStrategy.sendRedirect(request, response,
                "http://localhost:3000/auth/error?message=" + e.getMessage());
        }
    }

    private void handleExistingUser(DefaultOAuth2User oAuth2User, HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {

        Object idValue = oAuth2User.getAttribute("id");
        String providerId = String.valueOf(idValue);

        System.out.println(Optional.ofNullable(oAuth2User.getAttribute("id")));
        // DB에서 사용자 조회
        User user = userService.findByProviderId(providerId);
        log.info("CustomOAuth2UserService - kakaoId: {}", providerId);

        // 마지막 로그인 시간 업데이트
        user.updateLastLoginAt();
        userRepository.save(user);

        // JWT 토큰 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );
        String accessToken = jwtProvider.generateAccessToken(auth);
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());
        log.info("accessToken : {}", accessToken);
        log.info("refreshToken : {}", refreshToken);

        // RefreshToken 저장 (Redis 연동시 사용)
        refreshTokenService.store(user.getId(), refreshToken);

        String targetUrl = "http://localhost:3000/auth/callback" +
            "?accessToken=" + accessToken;

        log.info("기존 사용자 로그인 완료 - 리다이렉트: {}", targetUrl);
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private void handleNewUser(DefaultOAuth2User oAuth2User, HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {

        Object idValue = oAuth2User.getAttribute("id");
        String providerId = String.valueOf(idValue);
        String nickname = oAuth2User.getAttribute("extractedNickname");

        log.info("provider Id : " + providerId);
        // Base64 인코딩하여 URL 파라미터로 안전하게 전달
        String kakaoInfo = Base64.getEncoder().encodeToString(
            String.format("%s|%s|%s|%s",
                providerId,
                nickname,
                nickname,
                ""
            ).getBytes(StandardCharsets.UTF_8)
        );

        String targetUrl =
            "http://localhost:3000/auth/signup/oauth?provider=kakao&info=" + kakaoInfo;

        log.info("신규 사용자 회원가입 페이지로 리다이렉트: {}", targetUrl);
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}