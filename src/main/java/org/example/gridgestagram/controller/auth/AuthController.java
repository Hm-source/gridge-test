package org.example.gridgestagram.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.auth.dto.LoginRequest;
import org.example.gridgestagram.controller.auth.dto.LoginResponse;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TermsResponse;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshRequest;
import org.example.gridgestagram.controller.auth.dto.TokenRefreshResponse;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpRequest;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.TermsService;
import org.example.gridgestagram.service.facade.AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "회원가입, 로그인, 토큰 관리 등 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthFacade authFacade;
    private final TermsService termsService;


    @Operation(
        summary = "일반 회원가입",
        description = "이메일/전화번호 기반 일반 회원가입을 진행합니다. 이름, 아이디, 전화번호, 비밀번호, 생년월일, 약관 동의가 필요합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SignUpResponse.class),
                examples = @ExampleObject(
                    name = "회원가입 성공 응답",
                    value = "{\"id\": 1, \"username\": \"testuser\", \"name\": \"홍길동\", \"birthdate\": \"1990-01-01\", \"role\": \"USER\", \"createdAt\": \"2024-01-01T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효성 검증 실패)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"이름은 필수입니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 사용자",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"이미 존재하는 사용자입니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        )
    })
    @LogAction(value = LogType.USER_SIGNUP, targetType = "USER")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authFacade.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "OAuth2 회원가입",
        description = "카카오 OAuth2 로그인 후 추가 정보 입력을 통한 회원가입을 진행합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "OAuth2 회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OAuth2SignUpResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.USER_OAUTH_SIGNUP, targetType = "USER")
    @PostMapping("/oauth/signup")
    public ResponseEntity<OAuth2SignUpResponse> signUpOAuth2(
        @Valid @RequestBody OAuth2SignUpRequest request) {
        OAuth2SignUpResponse response = authFacade.signUpOAuth2(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "로그인",
        description = "아이디(username)와 비밀번호로 로그인을 진행합니다. 성공 시 JWT 토큰을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "로그인 성공 응답",
                    value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"tokenType\": \"Bearer\", \"user\": {\"id\": 1, \"username\": \"testuser\", \"name\": \"홍길동\"}, \"loginAt\": \"2024-01-01T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (필수 필드 누락)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"아이디는은 필수입니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (아이디 또는 비밀번호 불일치)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"인증에 실패했습니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        )
    })
    @LogAction(value = LogType.USER_LOGIN, targetType = "USER")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authFacade.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "토큰 갱신",
        description = "만료된 Access Token을 Refresh Token을 사용하여 새로운 Access Token으로 갱신합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "토큰 갱신 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenRefreshResponse.class),
                examples = @ExampleObject(
                    name = "토큰 갱신 성공 응답",
                    value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"tokenType\": \"Bearer\", \"issuedAt\": \"2024-01-01T10:00:00\", \"expiresIn\": 86400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (Refresh Token 누락)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"리프레시 토큰은 필수입니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 Refresh Token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\": \"유효하지 않은 토큰입니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        )
    })
    @LogAction(value = LogType.USER_AUTH_REFRESH, targetType = "USER")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
        @Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authFacade.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "로그아웃",
        description = "현재 로그인된 사용자를 로그아웃 처리합니다. JWT 토큰을 무효화합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "로그아웃 성공 응답",
                    value = "{\"message\": \"로그아웃이 완료되었습니다.\", \"timestamp\": \"2024-01-01T10:00:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.USER_LOGOUT, targetType = "USER")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authFacade.logout();
        return ResponseEntity.ok(Map.of(
            "message", "로그아웃이 완료되었습니다.",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    @Operation(
        summary = "활성화된 약관 조회",
        description = "현재 활성화되어 있는 모든 이용약관 목록을 조회합니다. 회원가입 시 동의가 필요한 약관들을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "약관 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TermsResponse.class),
                examples = @ExampleObject(
                    name = "약관 조회 성공 응답",
                    value = "[{\"id\": 1, \"title\": \"서비스 이용약관\", \"content\": \"본 약관은...\", \"isRequired\": true, \"createdAt\": \"2024-01-01T10:00:00\"}, {\"id\": 2, \"title\": \"개인정보 처리방침\", \"content\": \"개인정보...\", \"isRequired\": true, \"createdAt\": \"2024-01-01T10:00:00\"}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.USER_TERMS_VIEW, targetType = "USER")
    @GetMapping("/terms")
    public ResponseEntity<List<TermsResponse>> getActiveTerms() {
        List<TermsResponse> terms = termsService.getActiveTerms();
        return ResponseEntity.ok(terms);
    }
}