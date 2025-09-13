package org.example.gridgestagram.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "사용자 정보 수정 요청")
@Getter
@Builder
@AllArgsConstructor
public class UserRequest {

    @Schema(description = "사용자 이름 (1-20자)", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하입니다.")
    private String name;

    @Schema(description = "사용자 아이디 (1-20자)", example = "user123")
    @NotBlank(message = "아이디는은 필수입니다.")
    @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하입니다.")
    private String username;

    @Schema(description = "비밀번호 (6-20자)", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하입니다.")
    private String password;

    @Schema(description = "생년월일 (과거 날짜)", example = "1990-01-01")
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

}