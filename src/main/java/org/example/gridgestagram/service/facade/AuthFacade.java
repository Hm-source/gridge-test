package org.example.gridgestagram.service.facade;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.auth.dto.LoginRequest;
import org.example.gridgestagram.controller.auth.dto.LoginResponse;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshRequest;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshResponse;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpRequest;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpResponse;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.UserStatus;
import org.example.gridgestagram.security.JwtProvider;
import org.example.gridgestagram.service.domain.RefreshTokenService;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        UserStatus status = user.getStatus();

        if (status != UserStatus.ACTIVE) {
            throw new CustomException(ErrorCode.USER_NOT_ACTIVE);
        }

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
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
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
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("인증되지 않은 사용자의 로그아웃 시도");
            return;
        }

        String username;

        if (authentication.getPrincipal() instanceof User user) {
            username = user.getUsername();
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            log.warn("로그아웃 실패 - 알 수 없는 Principal 타입: {}",
                authentication.getPrincipal().getClass());
            return;
        }

        User user = userService.findByUsername(username);
        refreshTokenService.deleteByUserId(user.getId());

        // 선택사항: SecurityContext 클리어 (보통 Spring Security가 자동으로 처리)
        SecurityContextHolder.clearContext();
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

        // RefreshToken 저장
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

}
