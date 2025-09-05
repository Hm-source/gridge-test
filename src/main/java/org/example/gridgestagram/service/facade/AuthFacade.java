package org.example.gridgestagram.service.facade;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.auth.dto.LoginRequest;
import org.example.gridgestagram.controller.auth.dto.LoginResponse;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshRequest;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshResponse;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpRequest;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpResponse;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.security.JwtProvider;
import org.example.gridgestagram.service.domain.RefreshTokenService;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    @Value("${jwt.token.expiration}")
    private long accessTokenExpiration;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        userService.validateUsernameNotExists(request.getUsername());
        String defaultProfileImageUrl = "https://example.com/default-profile.png"; // 기본 프로필 이미지
        return userService.save(request, defaultProfileImageUrl);
    }


    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        user.updateLastLoginAt();
        userRepository.save(user);

        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .user(UserResponse.from(user))
            .loginAt(LocalDateTime.now())
            .build();
    }


    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userService.findByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );

        String newAccessToken = jwtProvider.generateAccessToken(authentication);

        return TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .tokenType("Bearer")
            .issuedAt(LocalDateTime.now())
            .expiresIn(accessTokenExpiration)
            .build();
    }

    @Transactional
    public void logout(Authentication authentication) {
        String username;

        if (authentication.getPrincipal() instanceof User user) {
            username = user.getUsername();
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            throw new IllegalArgumentException("지원하지 않는 인증 타입입니다.");
        }
        User user = userService.findByUsername(username);

        refreshTokenService.deleteByUserId(user.getId());

    }

    @Transactional
    public OAuth2SignUpResponse signUpOAuth2(OAuth2SignUpRequest request) {
        SignUpResponse signUpResponse = userService.saveOAuth2User(request);

        User user = userService.findById(signUpResponse.getId());
        // 토큰 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );
        String accessToken = jwtProvider.generateAccessToken(auth);
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        // RefreshToken 저장 (Redis 연동시 사용)
        refreshTokenService.store(user.getId(), refreshToken);

        return OAuth2SignUpResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .provider(user.getProvider().name())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Transactional
    public LoginResponse loginOAuth2(String providerId) {
        User user = userService.findByProviderId(providerId);

        // 마지막 로그인 시간 업데이트
        user.updateLastLoginAt();
        userRepository.save(user);

        // 토큰 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities()
        );
        String accessToken = jwtProvider.generateAccessToken(auth);
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        // RefreshToken 저장 (Redis 연동시 사용)
        refreshTokenService.store(user.getId(), refreshToken);

        return LoginResponse.from(accessToken, refreshToken, user);
    }
}
