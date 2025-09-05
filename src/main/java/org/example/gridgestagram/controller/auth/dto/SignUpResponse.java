package org.example.gridgestagram.controller.auth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.Role;

@AllArgsConstructor
@Getter
@Builder
public class SignUpResponse {

    private Long id;
    private String username;
    private String name;
    private LocalDate birthdate;
    private Role role;
    private LocalDateTime createdAt;

    public static SignUpResponse from(User user) {
        return SignUpResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .birthdate(user.getBirthdate())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
