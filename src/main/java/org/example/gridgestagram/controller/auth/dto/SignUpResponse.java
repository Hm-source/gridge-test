package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.Role;

@Schema(description = "회원가입 성공 응답 정보")
@AllArgsConstructor
@Getter
@Builder
public class SignUpResponse {

    @Schema(description = "사용자 고유 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 아이디", example = "user123")
    private String username;

    @Schema(description = "사용자 실명", example = "홍길동")
    private String name;

    @Schema(description = "생년월일", example = "1990-01-15")
    private LocalDate birthdate;

    @Schema(description = "사용자 권한", example = "USER")
    private Role role;

    @Schema(description = "계정 생성 일시", example = "2024-01-01 10:00:00")
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
