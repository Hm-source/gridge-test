package org.example.gridgestagram.controller.admin.dto;

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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserSearchCondition {

    // 기본 검색 조건
    private String username;        // 사용자 아이디로 검색
    private String name;           // 이름으로 검색
    private String phone;          // 전화번호로 검색

    // 회원 상태별 조회
    private UserStatus status;     // 회원 상태
    private List<UserStatus> statusList; // 여러 상태 OR 조건

    // 가입일 범위 검색
    private LocalDate joinDateFrom;   // 가입일 시작 (YYYYMMDD)
    private LocalDate joinDateTo;     // 가입일 종료

    // 최근 로그인 기준 검색
    private LocalDate lastLoginFrom;  // 최근 로그인 시작일
    private LocalDate lastLoginTo;    // 최근 로그인 종료일

    // 휴면 계정 조건
    private Boolean isDormant;        // 휴면 여부
    private Integer dormantDays;      // N일 이상 미접속

    // 약관 동의 상태
    private Boolean hasExpiredTerms;  // 만료된 약관 보유 여부
    private Long termsId;            // 특정 약관 동의 상태

    // 정렬 조건
    @Default
    private String sortBy = "createdAt";
    @Default// 정렬 필드
    private String sortDirection = "desc";   // 정렬 방향 (asc, desc)

    private Provider provider;       // 가입 경로 (일반, 카카오 등)
    private Role role;              // 권한 (USER, ADMIN)
    private SubscriptionStatus subscriptionStatus; // 구독 상태
}
