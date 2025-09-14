package org.example.gridgestagram.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.annotation.LogAction;
import org.example.gridgestagram.controller.admin.dto.AccountActionResponse;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.AccountManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 - 계정 관리", description = "관리자용 계정 상태 관리 API (일시정지, 해제, 휴면 활성화)")
@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@LogAction(value = LogType.ADMIN_USER_STATUS_CHANGE, targetType = "USER")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    @Operation(
        summary = "계정 일시정지",
        description = "관리자가 문제가 있는 사용자의 계정을 일시적으로 정지시킵니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "계정 일시정지 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AccountActionResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"계정이 일시정지되었습니다.\", \"action\": \"SUSPEND\", \"timestamp\": \"2024-01-01 10:00:00\"}"
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
    @PostMapping("/{userId}/suspend")
    public ResponseEntity<AccountActionResponse> suspendAccount(
        @Parameter(description = "일시정지할 사용자 ID", example = "1")
        @PathVariable Long userId) {

        accountManagementService.suspendAccount(userId);

        return ResponseEntity.ok(AccountActionResponse.builder()
            .success(true)
            .message("계정이 일시정지되었습니다.")
            .action("SUSPEND")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @Operation(
        summary = "계정 일시정지 해제",
        description = "관리자가 일시정지된 사용자의 계정을 다시 활성화시킵니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "계정 일시정지 해제 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AccountActionResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"계정 일시정지가 해제되었습니다.\", \"action\": \"UNSUSPEND\", \"timestamp\": \"2024-01-01 10:00:00\"}"
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
    @PostMapping("/{userId}/unsuspend")
    public ResponseEntity<AccountActionResponse> unsuspendAccount(
        @Parameter(description = "일시정지 해제할 사용자 ID", example = "1")
        @PathVariable Long userId) {

        accountManagementService.unsuspendAccount(userId);

        return ResponseEntity.ok(AccountActionResponse.builder()
            .success(true)
            .message("계정 일시정지가 해제되었습니다.")
            .action("UNSUSPEND")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @Operation(
        summary = "휴면 계정 활성화",
        description = "관리자가 휴면 상태인 사용자의 계정을 다시 활성화시킵니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "휴면 계정 활성화 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AccountActionResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"휴면 계정이 활성화되었습니다.\", \"action\": \"ACTIVATE_DORMANT\", \"timestamp\": \"2024-01-01 10:00:00\"}"
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
    @PostMapping("/{userId}/activate-dormant")
    public ResponseEntity<AccountActionResponse> activateDormantAccount(
        @Parameter(description = "활성화할 휴면 계정 사용자 ID", example = "1")
        @PathVariable Long userId) {

        accountManagementService.activateDormantAccount(userId);

        return ResponseEntity.ok(AccountActionResponse.builder()
            .success(true)
            .message("휴면 계정이 활성화되었습니다.")
            .action("ACTIVATE_DORMANT")
            .timestamp(LocalDateTime.now())
            .build());
    }
}
