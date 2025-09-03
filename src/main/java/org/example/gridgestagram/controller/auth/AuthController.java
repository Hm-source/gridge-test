package org.example.gridgestagram.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.auth.dto.LoginRequest;
import org.example.gridgestagram.controller.auth.dto.LoginResponse;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshRequest;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshResponse;
import org.example.gridgestagram.service.facade.AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authFacade.signUp(request);
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
//    @PreAuthorize("hasAnyRole('USER','PREMIUM_USER')")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃되었습니다. 토큰을 삭제해주세요.");
    }
}