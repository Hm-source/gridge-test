package org.example.gridgestagram.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.gridgestagram.repository.subscription.entity.vo.SubscriptionStatus;
import org.example.gridgestagram.repository.user.entity.vo.Provider;
import org.example.gridgestagram.repository.user.entity.vo.Role;
import org.example.gridgestagram.repository.user.entity.vo.UserStatus;

@Schema(description = "관리자용 사용자 검색 조건")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSearchCondition {

    @Schema(description = "사용자 아이디 검색", example = "user123")
    private String username;

    @Schema(description = "사용자 이름 검색", example = "홍길동")
    private String name;

    @Schema(description = "전화번호 검색", example = "01012345678")
    private String phone;

    @Schema(description = "회원 상태 필터", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "여러 상태 OR 조건 검색")
    private List<UserStatus> statusList;

    @Schema(description = "가입일 시작일", example = "2024-01-01")
    private LocalDate joinDateFrom;

    @Schema(description = "가입일 종료일", example = "2024-12-31")
    private LocalDate joinDateTo;

    @Schema(description = "마지막 로그인 시작일", example = "2024-01-01")
    private LocalDate lastLoginFrom;

    @Schema(description = "마지막 로그인 종료일", example = "2024-12-31")
    private LocalDate lastLoginTo;

    @Schema(description = "휴면 계정 여부", example = "true")
    private Boolean isDormant;

    @Schema(description = "N일 이상 미접속 기준", example = "30")
    private Integer dormantDays;

    @Schema(description = "만료된 약관 보유 여부", example = "false")
    private Boolean hasExpiredTerms;

    @Schema(description = "특정 약관 ID", example = "1")
    private Long termsId;

    @Schema(description = "정렬 필드", example = "createdAt")
    @Default
    private String sortBy = "createdAt";

    @Schema(description = "정렬 방향", example = "desc")
    @Default
    private String sortDirection = "desc";

    @Schema(description = "가입 경로 필터", example = "KAKAO")
    private Provider provider;

    @Schema(description = "권한 필터", example = "USER")
    private Role role;

    @Schema(description = "구독 상태 필터", example = "ACTIVE")
    private SubscriptionStatus subscriptionStatus;
}
