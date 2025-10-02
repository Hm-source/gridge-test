# Gridgestagram

[**wiki에 프로젝트 내용을 정리하였습니다**](https://github.com/Hm-source/gridgestagram/wiki)

- Spring Boot 기반의 소셜 미디어 플랫폼
- **프로젝트명**: gridgestagram
- **Java 버전**: 17
- **Spring Boot 버전**: 3.5.5
- **빌드 도구**: Gradle

# 기술 스택 & 아키텍처

## 핵심 기술 스택

### **백엔드 프레임워크**
- **Java 17** - LTS 버전으로 안정성과 보안 업데이트 보장
- **Spring Boot 3.5.5** - 자동 설정과 내장 서버로 복잡한 소셜 미디어 비즈니스 로직을 빠르게 구현
- **Spring Security** - 사용자 인증부터 콘텐츠 권한 관리까지 소셜 플랫폼 보안 요구사항을 통합 지원
- **Spring Data JPA** - 사용자-피드-구독 간 복잡한 엔티티 관계를 객체 지향적으로 관리

### **데이터베이스 & 저장소**
- **MySQL** - 사용자 관계와 피드 데이터의 복잡한 관계형 구조를 ACID 보장하며 안정적으로 처리
- **Redis** - 좋아요 카운트와 리프레시 토큰을 메모리에서 고속 처리하여 사용자 경험 향상
- **AWS S3** - 인스타그램 스타일의 대용량 미디어 파일을 무제한 확장 가능하게 저장

### **개발 도구 & 라이브러리**
- **QueryDSL** - 피드 검색과 사용자 필터링 등 복잡한 동적 쿼리를 컴파일 시점에 검증
- **JWT** - 확장성을 위한 무상태 인증으로 서버 부하 최소화
- **Lombok** - 엔티티와 DTO의 반복적인 getter/setter 코드를 자동 생성하여 개발 속도 향상
- **Gradle** - Maven 대비 빠른 빌드와 멀티 모듈 프로젝트 관리의 유연성 제공

### **운영 & 배포**
- **Docker** - 개발/프로덕션 환경 일관성 보장과 마이크로서비스 확장 기반 마련
- **AWS Parameter Store** - 데이터베이스 연결 정보와 API 키를 암호화하여 중앙 집중 관리
- **Swagger** - 프론트엔드 팀과의 협업 효율성을 위한 실시간 API 명세 및 테스트 환경 제공

---

## 프로젝트 구조

```
src/main/java/org/example/gridgestagram/
├── 📄 GridgestagramApplication.java       # 애플리케이션 진입점
│
├── 🏷️ annotation/                         # 커스텀 어노테이션
│   ├── LogAction.java                    # 로그 액션 마킹
│   └── LoggingAspect.java                # AOP 로깅 구현
│
├── ⚙️ config/                            # 시스템 설정
│   ├── AsyncConfig.java                  # 비동기 처리
│   ├── CacheConfig.java                  # 캐시 전략
│   ├── SecurityConfig.java               # 보안 정책
│   ├── RedisConfig.java                  # Redis 연결
│   ├── S3Config.java                     # AWS S3 설정
│   └── SwaggerConfig.java                # API 문서화
│
├── 🌐 controller/                        # REST API 엔드포인트
│   ├── auth/                             # 로그인, 회원가입
│   ├── user/                             # 사용자 프로필 관리
│   ├── feed/                             # 피드 CRUD 및 좋아요
│   ├── subscription/                     # 구독 관리
│   ├── file/                             # 파일 업로드/다운로드
│   └── admin/                            # 관리자 기능
│
├── 🗃️ repository/                        # 데이터 액세스 계층
│   ├── user/                             # 사용자 데이터
│   ├── feed/                             # 피드 데이터
│   ├── subscription/                     # 구독 관계
│   ├── files/                            # 파일 메타데이터
│   └── log/                              # 시스템 로그
│
├── 🔧 service/                           # 비즈니스 로직
│   ├── domain/                           # 핵심 도메인 서비스
│   └── facade/                           # 복합 서비스 조합
│
├── 🔐 security/                          # 보안 구현체
├── ❌ exceptions/                        # 예외 처리
├── 📊 data/                              # 데이터 전송 객체
└── 🛠️ utils/                            # 공통 유틸리티
```

---

## 아키텍처 패턴 & 설계 철학

### **도메인 중심 설계 (DDD)**
비즈니스 도메인을 중심으로 코드를 구성하여 복잡한 소셜 미디어 로직을 직관적으로 이해할 수 있도록 설계했습니다. 사용자, 피드 등 각 도메인이 명확히 분리되어 유지보수와 확장이 용이합니다.

### **헥사고날 아키텍처 (Ports & Adapters)**
비즈니스 로직을 외부 의존성(데이터베이스, 외부 API)으로부터 완전히 분리했습니다. 이를 통해 데이터베이스나 파일 저장소를 변경하더라도 핵심 비즈니스 로직은 영향받지 않아 테스트와 확장성이 크게 향상됩니다.

### **파사드 패턴 (Facade Pattern)**
여러 도메인 서비스를 조합하는 복잡한 비즈니스 플로우를 파사드로 단순화했습니다. 예를 들어, 피드 생성 시 이미지 업로드, 메타데이터 저장, 알림 발송 등의 과정을 하나의 파사드로 통합하여 클라이언트가 쉽게 사용할 수 있도록 구성했습니다.

### **관리자 기능**
소셜 플랫폼 운영에 필수적인 관리자 기능을 체계적으로 구현했습니다:

- 사용자 관리: 회원 정보 조회, 계정 상태 관리, 신고 처리
- 콘텐츠 모니터링: 부적절한 피드 검토 및 제재, 대량 콘텐츠 관리
- 시스템 모니터링: 서비스 상태 확인, 로그 분석, 성능 지표 추적
- 권한 분리: 관리자, 유저 역할을 명확히 구분하여 보안성과 운영 효율성 확보

### **성능 중심 캐싱 전략**
소셜 미디어의 특성상 좋아요와 인증 토큰은 매우 빈번하게 접근됩니다:
- **좋아요 캐싱**: 실시간 카운트 업데이트를 위해 Redis에서 고속 처리
- **리프레시 토큰 관리**: 사용자 세션의 빠른 검증과 갱신을 위해 메모리 기반 저장

### **확장성과 보안의 균형**
- **무상태 JWT 인증**: 서버 확장 시 세션 동기화 부담 없이 수평 확장 가능

---


## ERD
<img width="4020" height="1682" alt="gridge-test (2)" src="https://github.com/user-attachments/assets/e51d54ef-cb8e-4ee0-add6-b8028e54334b" />

## System Architecture
<img width="1140" height="641" alt="gridgestagram architecture drawio (4)" src="https://github.com/user-attachments/assets/d5264490-0877-4982-be92-85e05d5a9d74" />



## 주요 기능

### 🔐 인증 시스템 (Authentication System)

#### 1. 일반 회원가입/로그인
```http
POST /api/auth/signup
POST /api/auth/login
```
- **이메일/전화번호 기반 회원가입**: 이름, 아이디, 전화번호, 비밀번호, 생년월일, 약관 동의 필요
- **JWT 토큰 기반 인증**: Access Token과 Refresh Token으로 구성된 이중 토큰 시스템
- **비밀번호 암호화**: BCrypt를 사용한 안전한 비밀번호 저장
- **유효성 검증**: Spring Validation을 통한 입력 데이터 검증

#### 2. OAuth 2.0 소셜 로그인 (카카오)
```http
GET /oauth2/authorization/kakao
POST /api/auth/oauth/signup
```
- **카카오 OAuth 2.0 연동**: 카카오 계정을 통한 간편 로그인
- **신규/기존 사용자 구분**: OAuth 인증 후 자동으로 신규 사용자와 기존 사용자를 구분
- **추가 정보 수집**: 신규 사용자의 경우 추가 정보 입력을 통한 회원가입 완료
- **보안 처리**: Base64 인코딩을 통한 OAuth 정보 안전 전달

**구현 특징:**
- `CustomOAuth2UserService`: 카카오에서 받은 사용자 정보를 처리하고 신규/기존 사용자 판별
- `OAuth2AuthenticationSuccessHandler`: 인증 성공 후 적절한 페이지로 리다이렉트
- 신규 사용자: 회원가입 페이지로 리다이렉트 (사용자 정보 Base64 인코딩하여 전달)
   - 신규 사용자는 providerId+username 기준으로 한다. (회원가입 시 email 정보를 받지 않아 providerId로 대체함)
- 기존 사용자: JWT 토큰 발급 후 메인 페이지로 리다이렉트

#### 3. JWT 토큰 관리
```http
POST /api/auth/refresh
POST /api/auth/logout
```
- **Access Token**: 짧은 수명(1시간)의 API 접근 토큰
- **Refresh Token**: 긴 수명(7일)의 토큰 갱신용 토큰
- **토큰 갱신**: Refresh Token을 사용한 Access Token 자동 갱신
- **로그아웃**: 서버 측 토큰 무효화 처리

**구현 세부사항:**
- `JwtAuthenticationFilter`: 모든 요청에서 JWT 토큰을 검증
- `JwtAuthenticationProvider`: JWT 토큰 인증 처리
- `SecurityConfig`: Spring Security 설정으로 인증/인가 규칙 정의
- Stateless 세션 관리로 확장성 확보

#### 4. 보안 설정
- **CORS 설정**: 프론트엔드와의 안전한 통신
- **CSRF 비활성화**: REST API 특성상 CSRF 보호 비활성화
- **인증 제외 경로**: 회원가입, 로그인, Swagger 문서 등 공개 API
- **역할 기반 접근 제어**: USER, ADMIN 등 역할별 권한 관리

#### 5. 약관 관리 및 개인정보 동의 갱신
```http
GET /api/auth/terms
```
- **동적 약관 시스템**: 데이터베이스 기반 약관 관리
- **필수/선택 약관 구분**: 회원가입 시 필수 약관 동의 검증
- **약관 버전 관리**: 약관 변경 시 이력 관리

**개인정보 동의 자동 갱신 시스템:**
```java
@Scheduled(cron = "0 0 9 * * ?")  // 매일 오전 9시 실행
public void sendPrivacyConsent1DayNotice() {
    renewalNotificationService.sendPrivacyConsentRenewalNotifications(1);
}
```

- **스케줄러 기반 자동 알림**: 개인정보 처리 동의 만료 1일 전 자동 알림 발송
- **대상자 자동 식별**: nextAgreedDate를 기준으로 갱신 대상 사용자를 효율적 추출

**구현 특징:**
```java
// RenewalNotificationService.java - 갱신 대상자 추출 로직
@Transactional
public List<PrivacyConsentRenewalTarget> getPrivacyConsentRenewalTargets(int daysBeforeExpiry) {
    LocalDate targetDate = LocalDate.now().plusDays(daysBeforeExpiry);
    // 데이터 정책을 개인정보 관련 정책이라고 가정하고 진행함.
    Terms privacyPolicyTerms = termsRepository.findByTitleContaining("데이터")
        .orElseThrow(() -> new CustomException(ErrorCode.TERMS_NOT_FOUND));

    // nextAgreedDate가 대상일과 일치하는 사용자 조회
    List<UserTerms> expiringUserTerms = userTermsRepository
        .findByTermsIdAndNextAgreedDateAndIsAgreed(
            privacyPolicyTerms.getId(), targetDate, true
        );

    return expiringUserTerms.stream()
        .map(userTerms -> PrivacyConsentRenewalTarget.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .phone(user.getPhone())
            .daysUntilExpiry(ChronoUnit.DAYS.between(LocalDate.now(), userTerms.getNextAgreedDate()))
            .build())
        .toList();
}
```

#### 6. SMS 기반 비밀번호 재설정 시스템
```http
POST /api/auth/password/reset/request      # 비밀번호 재설정 요청
POST /api/auth/password/reset/verify       # SMS 인증 코드 확인
POST /api/auth/password/reset/complete     # 새 비밀번호 설정
POST /api/auth/password/reset/resend       # 인증 코드 재전송
```

**3단계 보안 인증 프로세스:**
1. **전화번호 인증**: 등록된 전화번호로만 비밀번호 재설정 가능
2. **SMS 인증 코드**: SecureRandom으로 생성된 6자리 숫자 코드
3. **임시 토큰 발급**: 인증 성공 시 비밀번호 변경 전용 64자리 보안 토큰 생성

**Redis 기반 보안 강화 시스템:**
- **무차별 대입 공격 방지**: Redis 카운터로 시도 횟수 실시간 추적
  - 비밀번호 재설정 요청: 1시간당 최대 5회 (`password_reset_attempt:{identifier}`)
  - 인증 코드 확인: 10분당 최대 5회 (`verify_attempt:{token}`)
- **자동 만료 관리**: Redis TTL을 활용한 시간 기반 제한 해제
- **토큰 상태 관리**: 1시간 유효한 UUID 기반 보안 토큰

**핵심 보안 구현:**
```java
// PasswordResetService.java - Redis 기반 시도 횟수 제한
private void checkResetAttemptLimit(String identifier) {
    String key = RESET_ATTEMPT_KEY + identifier;  // "password_reset_attempt:{phone}"
    Object attempts = redisTemplate.opsForValue().get(key);

    if (attempts != null && ((Number) attempts).intValue() >= MAX_RESET_ATTEMPTS) {
        throw new CustomException(ErrorCode.TOO_MANY_RESET_ATTEMPTS);
    }
}

private void incrementResetAttempt(String identifier) {
    String key = RESET_ATTEMPT_KEY + identifier;
    redisTemplate.opsForValue().increment(key);
    redisTemplate.expire(key, Duration.ofHours(1));  // 1시간 후 자동 삭제
}

// 보안 토큰 및 인증 코드 생성
private String generateSecureToken() {
    return UUID.randomUUID().toString().replace("-", "") +
           UUID.randomUUID().toString().replace("-", "");  // 64자리 토큰
}

private String generateVerificationCode() {
    Random random = new SecureRandom();
    int code = 100000 + random.nextInt(900000);  // 6자리 랜덤 숫자
    return String.valueOf(code);
}
```

**Entity 기반 상태 관리:**
```java
// PasswordResetToken.java - 토큰 생명주기 관리
@Entity
public class PasswordResetToken {
    private String token;                    // 64자리 UUID 보안 토큰
    private String verificationCode;         // 6자리 SMS 인증 코드
    private LocalDateTime expiresAt;         // 1시간 만료 시간
    private boolean isUsed;                  // 사용 완료 여부
    private boolean isVerified;              // SMS 인증 완료 여부

    public boolean isValid() {
        return !isUsed && !isExpired() && isVerified;  // 3조건 모두 만족 시만 유효
    }
}
```

**비밀번호 변경 시 추가 보안 검증:**
- 길이 및 형식 검증
- 기존 비밀번호와 동일성 검사 (BCrypt 해싱 비교)
- 정규식 검증: 영문+숫자 조합 필수

**시스템 특장점:**
- **보안**: 전화번호 → SMS 코드 → 보안 토큰 → 비밀번호 검증
- **실시간 공격 탐지**: Redis 기반 시도 횟수 추적으로 무차별 공격 실시간 차단
- **토큰 생명주기 관리**: 자동 만료, 일회성 사용, 기존 토큰 무효화로 보안 강화
- **확장 가능 설계**: VerificationMethod enum으로 EMAIL 등 다른 인증 방식 확장 준비

### 📱 피드 관리 시스템 (Feed Management System)

#### 1. Presigned URL을 통한 효율적인 파일 업로드
```http
POST /api/files/presigned-url          # 단일 파일 ( profile 이미지 용으로 따로 만들어 둠 )
POST /api/files/presigned-urls/batch   # 다중 파일 (최대 10개)
```
- **서버 부하 최소화**: 클라이언트가 AWS S3에 직접 파일을 업로드하여 서버 리소스 절약
- **보안성**: 15분 제한 시간의 Presigned URL로 안전한 업로드 환경 제공
- **파일 검증**: 업로드 완료 후 S3 객체 존재 여부를 확인하여 데이터 무결성 보장
- **다중 파일 지원**: 배치 처리를 통한 여러 파일 동시 업로드 최적화

**구현 특징:**
```java
// S3Facade.java의 핵심 로직
public PresignedUrlResponse generatePresignedUploadUrl(String fileName, String contentType) {
    String objectKey = generateObjectKey(fileName);
    Date expiration = new Date(System.currentTimeMillis() + (15 * 60 * 1000)); // 15분

    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
        .withMethod(HttpMethod.PUT)
        .withExpiration(expiration);

    URL presignedUrl = amazonS3.generatePresignedUrl(request);
    String finalUrl = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucketName, objectKey);

    return PresignedUrlResponse.builder()
        .presignedUrl(presignedUrl.toString())
        .finalUrl(finalUrl)  // 피드 생성 시 사용할 최종 URL
        .expiresIn(900)     // 15분 만료
        .build();
}
```

#### 2. 페이지네이션과 쿼리 최적화
```http
GET /api/feeds?page=0&size=10
GET /api/feeds/{feedId}
```
- **효율적인 페이지네이션**: Spring Data의 Pageable을 활용한 메모리 효율적인 데이터 조회
- **댓글 제한 로딩**: 피드 목록 조회 시 최신 댓글 3개만 로드
- **N+1 문제 방지**: Fetch Join을 통한 연관 엔티티 일괄 조회로 데이터베이스 쿼리 최적화

**쿼리 최적화 예시:**
```sql
-- JPQL에서의 Fetch Join 활용 (FeedRepository.java)
@Query("SELECT f FROM Feed f " +
       "LEFT JOIN FETCH f.user " +      -- 사용자 정보 즉시 로딩
       "LEFT JOIN FETCH f.files " +     -- 파일 정보 즉시 로딩
       "WHERE f.id = :feedId AND f.status = 'ACTIVE'")
Optional<Feed> findByIdWithUserAndFiles(@Param("feedId") Long feedId);
```

#### 3. Redis 기반 좋아요 시스템
```http
POST /api/feeds/{feedId}/likes      # 좋아요 토글
GET /api/feeds/{feedId}/likes/status # 좋아요 상태 조회
GET /api/feeds/{feedId}/likes/users  # 좋아요 누른 사용자 목록
```

**Redis 캐싱 전략:**
- **실시간 성능**: 좋아요 데이터를 Redis에 캐싱하여 밀리초 단위 응답 시간 달성
- **대용량 트래픽 대응**: 동시 다발적인 좋아요 요청에 대한 무손실 처리
- **비동기 DB 동기화**: 스케줄러를 통한 5초마다 Redis-DB 자동 동기화로 데이터 일관성 유지

**핵심 구현 로직:**
```java
// RedisLikeFacade.java - Redis Transaction으로 원자성 보장
private LikeToggleResponse addLike(Long feedId, Long userId, String likeCountKey,
                                  String likeUsersKey, String userLikedKey) {
    long timestamp = System.currentTimeMillis();

    stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
        @Override
        public List<Object> execute(RedisOperations operations) throws DataAccessException {
            operations.multi();  // 트랜잭션 시작

            operations.opsForValue().increment(likeCountKey, 1);              // 좋아요 수 증가
            operations.opsForZSet().add(likeUsersKey, userId.toString(), timestamp);  // 사용자 추가
            operations.opsForZSet().add(userLikedKey, feedId.toString(), timestamp);  // 역인덱스

            return operations.exec();  // 트랜잭션 커밋
        }
    });

    addToSyncQueue(feedId, userId, "ADD");  // DB 동기화 큐에 추가
}
```

```java
// LikeSyncScheduler.java - 주기적 DB 동기화
@Scheduled(fixedDelay = 5000)  // 5초마다 실행
@Transactional
public void syncLikesToDatabase() {
    int batchSize = 100;
    List<Object> syncItems = new ArrayList<>();

    // Redis 큐에서 배치 단위로 동기화 작업 처리
    for (int i = 0; i < batchSize; i++) {
        Object item = redisTemplate.opsForList().rightPop(LIKE_SYNC_QUEUE_KEY);
        if (item == null) break;
        syncItems.add(item);
    }

    // DB 동기화 처리 로직
    for (Object item : syncItems) {
        processSyncItem((Map<String, Object>) item);
    }
}
```

**성능 최적화 효과:**
- **응답 속도**: 좋아요 토글 응답시간 개선
- **동시성**: Redis Transaction으로 동시 좋아요 요청 시 Race Condition 방지

#### 4. 피드 CRUD 기능
```http
POST /api/feeds              # 피드 작성
GET /api/feeds              # 피드 목록 조회 (페이지네이션)
GET /api/feeds/{feedId}     # 피드 상세 조회
PUT /api/feeds/{feedId}     # 피드 수정
DELETE /api/feeds/{feedId}  # 피드 삭제
```

- **권한 기반 접근제어**: 작성자만 수정/삭제 가능한 보안 구조
- **논리 삭제**: 데이터 보존을 위한 소프트 딜리트 방식 채택
- **신고 시스템**: 부적절한 콘텐츠 신고 및 관리자 검토 워크플로우

### 🛠️ 관리자 시스템 (Admin Management System)

#### 1. QueryDSL 기반 동적 검색 시스템
```http
GET /api/admin/users/search?username=user123&status=ACTIVE&joinDateFrom=2024-01-01
GET /api/admin/feeds?userId=1&content=날씨&startDate=2024-01-01&endDate=2024-12-31
```
- **다차원 검색 조건**: 다양한 조건으로 사용자/피드 검색 가능 (이름, 상태, 기간, 가입경로 등)
- **동적 쿼리 생성**: 입력된 조건에 따라 런타임에 최적화된 SQL 쿼리 자동 생성
- **타입 안전성**: 컴파일 시점에 쿼리 오류 검증으로 안정성 확보
- **성능 최적화**: 필요한 조건만 WHERE 절에 추가하여 불필요한 연산 방지

**QueryDSL 구현 예시:**
```java
// UserRepositoryCustomImpl.java - 동적 조건 생성
private BooleanBuilder buildWhereClause(UserSearchCondition condition, QUser user,
                                      QUserTerms userTerms, QTerms terms) {
    BooleanBuilder builder = new BooleanBuilder();

    // 사용자명 검색 (LIKE 연산)
    if (StringUtils.hasText(condition.getUsername())) {
        builder.and(user.username.containsIgnoreCase(condition.getUsername()));
    }

    // 복수 상태 OR 조건
    if (condition.getStatusList() != null && !condition.getStatusList().isEmpty()) {
        builder.and(user.status.in(condition.getStatusList()));
    }

    // 가입일 범위 검색
    if (condition.getJoinDateFrom() != null) {
        builder.and(user.createdAt.goe(condition.getJoinDateFrom().atStartOfDay()));
    }

    // 휴면 계정 조건 (N일 이상 미접속)
    if (Boolean.TRUE.equals(condition.getIsDormant())) {
        LocalDateTime dormantCriteria = LocalDateTime.now()
            .minusDays(condition.getDormantDays() != null ? condition.getDormantDays() : 30);
        builder.and(user.lastLoginAt.lt(dormantCriteria));
    }

    return builder;
}
```

**검색 조건의 다양성:**
- **사용자 검색**: 아이디, 이름, 전화번호, 상태, 가입일, 로그인일, 가입경로, 권한, 휴면상태 등
- **피드 검색**: 작성자, 내용, 상태, 작성일, 좋아요 수, 댓글 수 등
- **로그 검색**: 사용자, 액션 타입, 카테고리, 실행일시 등

#### 2. AOP 기반 통합 로깅 시스템
```java
@LogAction(value = LogType.USER_LOGIN, targetType = "USER")
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    // 메서드 실행 전후로 자동 로그 기록
}
```

**커스텀 어노테이션의 장점:**
- **코드 중복 제거**: 수백 개의 API 엔드포인트에서 로깅 코드 중복 없이 일관된 로그 수집
- **관심사 분리**: 비즈니스 로직과 로깅 로직의 완전 분리로 코드 가독성 향상

**로깅 시스템 구현 특징:**
```java
// LoggingAspect.java - AOP Around 어드바이스
@Around("@annotation(logAction)")
public Object logMethodExecution(ProceedingJoinPoint joinPoint, LogAction logAction)
    throws Throwable {

    User currentUser = getCurrentUser();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    try {
        Object result = joinPoint.proceed();
        recordSuccessLog(logAction, currentUser, methodName, args, result);
        return result;
    } catch (Exception e) {
        recordFailureLog(logAction, currentUser, methodName, args, e);
        throw e;
    }
}
```

**로그 카테고리 분류:**
- **회원 관리**: 로그인, 가입, 탈퇴 등
- **피드 관리**: 작성, 수정, 삭제, 좋아요 등
- **관리자**: 사용자 관리, 피드 관리, 신고 처리 등
- **보안**: 인증 실패, 권한 오류 등

#### 3. 통합 관리자 대시보드
```http
GET /api/admin/users           # 사용자 관리
GET /api/admin/feeds                  # 피드 관리
GET /api/admin/reports                # 신고 관리
GET /api/admin/logs                   # 시스템 로그 조회
```

**핵심 관리 기능:**
- **사용자 관리**: 검색, 상세조회, 계정 정지/해제, 휴면 활성화
- **콘텐츠 관리**: 피드 검색/삭제, 신고 처리, 부적절 콘텐츠 제재
- **시스템 모니터링**: 실시간 로그 조회, 사용자 활동 분석, 보안 이벤트 추적
- **권한 관리**: `@Secured("ROLE_ADMIN")` 어노테이션으로 관리자 전용 API 보호

#### 4. 고급 검색 및 필터링 기능

**복합 조건 검색 예시:**
```java
// 휴면 계정 + 만료된 약관 보유 사용자 검색
UserSearchCondition condition = UserSearchCondition.builder()
    .isDormant(true)
    .dormantDays(90)
    .hasExpiredTerms(true)
    .statusList(List.of(UserStatus.ACTIVE, UserStatus.DORMANT))
    .joinDateFrom(LocalDate.of(2023, 1, 1))
    .build();
```

### 기타
- **API 문서화**: Swagger를 통한 실시간 API 문서 및 테스트 환경

## 설정 파일

- `application.yml` - 기본 설정
- `application-local.yml` - 로컬 환경 설정
- `application-prod.yml` - 프로덕션 환경 설정
