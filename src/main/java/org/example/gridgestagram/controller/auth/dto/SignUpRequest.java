package org.example.gridgestagram.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "일반 회원가입 요청 정보")
@Getter
@Builder
@AllArgsConstructor
public class SignUpRequest {

    @Schema(description = "사용자 실명", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하입니다.")
    private String name;

    @Schema(description = "사용자 아이디 (로그인 시 사용)", example = "user123")
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 1, max = 20, message = "아이디는 1자 이상 20자 이하입니다.")
    private String username;

    @Schema(description = "휴대폰 번호 (인증 및 연락처)", example = "01012345678")
    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @Schema(description = "비밀번호 (6-20자)", example = "password123")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하입니다.")
    private String password;

    @Schema(description = "생년월일 (과거 날짜만 입력 가능)", example = "1990-01-15")
    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate;

    @Schema(description = "이용약관 동의 정보")
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