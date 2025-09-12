package org.example.gridgestagram.controller.admin;

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

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN")
@Validated
public class AdminUserController {

    private final AdminUserService adminUserService;

    @LogAction(value = LogType.ADMIN_USER_VIEW, targetType = "USER")
    @GetMapping("/search")
    public ResponseEntity<Page<UserDetailResponse>> searchUsers(
        @ModelAttribute UserSearchCondition condition,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDetailResponse> users = adminUserService.searchUsers(condition, pageable);

        return ResponseEntity.ok(users);
    }

    @LogAction(value = LogType.ADMIN_USER_VIEW, targetType = "USER")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponse> getUserDetail(@PathVariable Long userId) {
        UserDetailResponse user = adminUserService.getUserDetail(userId);
        return ResponseEntity.ok(user);
    }

}
