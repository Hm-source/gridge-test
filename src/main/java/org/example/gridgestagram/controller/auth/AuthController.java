package org.example.gridgestagram.controller.auth;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.auth.dto.LoginRequest;
import org.example.gridgestagram.controller.auth.dto.LoginResponse;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TermsResponse;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshRequest;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshResponse;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpRequest;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpResponse;
import org.example.gridgestagram.service.domain.TermsService;
import org.example.gridgestagram.service.facade.AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthFacade authFacade;
    private final TermsService termsService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authFacade.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/oauth/signup")
    public ResponseEntity<OAuth2SignUpResponse> signUpOAuth2(
        @Valid @RequestBody OAuth2SignUpRequest request) {
        OAuth2SignUpResponse response = authFacade.signUpOAuth2(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authFacade.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
        @Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authFacade.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        authFacade.logout(authentication);
        return ResponseEntity.ok(Map.of(
            "message", "로그아웃이 완료되었습니다.",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/terms")
    public ResponseEntity<List<TermsResponse>> getActiveTerms() {
        List<TermsResponse> terms = termsService.getActiveTerms();
        return ResponseEntity.ok(terms);
    }
}