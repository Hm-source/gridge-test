package org.example.gridgestagram.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal String username) {
        log.info(username);
        User user = userService.findByUsername(username);
        return UserResponse.from(user);
    }
}
