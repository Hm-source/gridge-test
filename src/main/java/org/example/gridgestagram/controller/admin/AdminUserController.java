package org.example.gridgestagram.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.admin.dto.UserDetailResponse;
import org.example.gridgestagram.controller.admin.dto.UserSearchCondition;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 - 사용자 관리", description = "관리자용 사용자 검색, 상세 조회 관리 API")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@Validated
public class AdminUserController {

    private final AdminUserService adminUserService;


    @Operation(
        summary = "사용자 검색",
        description = "관리자가 다양한 조건으로 사용자를 검색합니다. 사용자명, 이메일, 상태, 가입일 등으로 필터링할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "사용자 검색 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"content\": [{\"id\": 1, \"username\": \"user123\", \"email\": \"user@example.com\", \"status\": \"ACTIVE\", \"feedCount\": 25, \"followerCount\": 100, \"followingCount\": 50, \"createdAt\": \"2024-01-01 10:00:00\"}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 20}, \"totalElements\": 500}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.ADMIN_USER_VIEW, targetType = "USER")
    @GetMapping("")
    public ResponseEntity<Page<UserDetailResponse>> searchUsers(
        @Parameter(description = "검색 조건 (사용자명, 사용자 아이디, 상태, 기간 등)")
        @ModelAttribute UserSearchCondition condition,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기 (기본 20개)", example = "20")
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDetailResponse> users = adminUserService.searchUsers(condition, pageable);

        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "사용자 상세 조회",
        description = "관리자가 특정 사용자의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "사용자 상세 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDetailResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"username\": \"user123\", \"email\": \"user@example.com\", \"realName\": \"홍길동\", \"status\": \"ACTIVE\", \"profileImageUrl\": \"https://example.com/profile.jpg\", \"bio\": \"안녕하세요!\", \"feedCount\": 25, \"followerCount\": 100, \"followingCount\": 50, \"totalLikesReceived\": 500, \"createdAt\": \"2024-01-01 10:00:00\", \"lastLoginAt\": \"2024-12-01 15:30:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 부족 (관리자 권한 필요)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @LogAction(value = LogType.ADMIN_USER_VIEW, targetType = "USER")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponse> getUserDetail(
        @Parameter(description = "상세 조회할 사용자 ID", example = "1")
        @PathVariable Long userId) {
        UserDetailResponse user = adminUserService.getUserDetail(userId);
        return ResponseEntity.ok(user);
    }

}
