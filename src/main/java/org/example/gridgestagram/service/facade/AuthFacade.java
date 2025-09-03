package org.example.gridgestagram.service.facade;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.auth.dto.LoginRequest;
import org.example.gridgestagram.controller.auth.dto.LoginResponse;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshRequest;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshResponse;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.security.JwtProvider;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
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
}
