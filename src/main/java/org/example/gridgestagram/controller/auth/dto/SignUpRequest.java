package org.example.gridgestagram.controller.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.gridgestagram.repository.user.entity.User;

@Getter
@Builder
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하입니다.")
    private String name;

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하입니다.")
    private String username;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하입니다.")
    private String password;

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

    @Valid
    @NotNull(message = "이용약관 동의 정보는 필수입니다.")
    private TermsAgreementRequest termsAgreement;

    public User toEntity(String encodedPassword, String profileImageUrl) {
        return User.createBasicUser(
            this.username,
            this.name,
            encodedPassword,
            this.phone,
            this.birthdate,
            profileImageUrl
        );
    }
}