package org.example.gridgestagram.controller.admin;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.AccountActionResponse;
import org.example.gridgestagram.service.domain.AccountManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    @PostMapping("/{userId}/suspend")
    public ResponseEntity<AccountActionResponse> suspendAccount(
        @PathVariable Long userId) {

        accountManagementService.suspendAccount(userId);

        return ResponseEntity.ok(AccountActionResponse.builder()
            .success(true)
            .message("계정이 일시정지되었습니다.")
            .action("SUSPEND")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @PostMapping("/{userId}/unsuspend")
    public ResponseEntity<AccountActionResponse> unsuspendAccount(@PathVariable Long userId) {

        accountManagementService.unsuspendAccount(userId);

        return ResponseEntity.ok(AccountActionResponse.builder()
            .success(true)
            .message("계정 일시정지가 해제되었습니다.")
            .action("UNSUSPEND")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @PostMapping("/{userId}/activate-dormant")
    public ResponseEntity<AccountActionResponse> activateDormantAccount(@PathVariable Long userId) {

        accountManagementService.activateDormantAccount(userId);

        return ResponseEntity.ok(AccountActionResponse.builder()
            .success(true)
            .message("휴면 계정이 활성화되었습니다.")
            .action("ACTIVATE_DORMANT")
            .timestamp(LocalDateTime.now())
            .build());
    }
}
