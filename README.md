# Gridgestagram

Instagram 클론 프로젝트 - Spring Boot 기반의 소셜 미디어 플랫폼

## 프로젝트 개요

- **프로젝트명**: gridgestagram
- **Java 버전**: 17
- **Spring Boot 버전**: 3.5.5
- **빌드 도구**: Gradle

## 기술 스택

| 카테고리 | 기술 | 설명 | 선택 이유                                                                              |
|---------|------|------|------------------------------------------------------------------------------------|
| **프레임워크** | Spring Boot 3.5.5 | 메인 애플리케이션 프레임워크 | 소셜 미디어의 복잡한 비즈니스 로직과 다양한 API 엔드포인트를 효율적으로 관리하며, 자동 설정과 내장 서버로 빠른 개발과 배포가 가능        |
| **보안** | Spring Security | 인증/인가 관리 | 사용자 계정, 개인정보, 콘텐츠 보호가 핵심인 소셜 플랫폼에서 OAuth2, JWT 토큰 인증, 역할 기반 권한 관리를 통합적으로 제공        |
| **데이터베이스** | MySQL | 메인 데이터베이스 | 사용자, 피드, 팔로우 관계 등 복잡한 관계형 데이터 구조를 안정적으로 처리하며, 트랜잭션 일관성과 ACID 속성을 보장하여 데이터 무결성 확보   |
| **ORM** | Spring Data JPA | 객체-관계 매핑 | 복잡한 엔티티 관계(User-Feed-Subscription 등)를 객체 지향적으로 관리하고, 반복적인 CRUD 작업을 자동화하여 개발 생산성 향상 |
| **쿼리** | QueryDSL | 타입 안전한 쿼리 작성 | 피드 검색, 사용자 필터링, 구독 이력 조회 등 복잡한 동적 쿼리를 컴파일 시점에 검증하여 런타임 오류를 방지하고 유지보수성 향상           |
| **인증** | JWT | 토큰 기반 인증 시스템 | Stateless 인증을 지원하며, 확장성이 뛰어나고 서버 부하를 줄이면서 사용자 세션을 안전하게 관리        |
| **캐싱** | Redis | 캐싱 및 세션 관리 | 피드 좋아요 데이터를 메모리에 캐싱하여 응답 속도를 개선하고, 데이터베이스 부하를 줄여 처리 능력 향상                          |
| **파일 저장소** | AWS S3 | 이미지/동영상 파일 저장 | Instagram과 같은 미디어 중심 플랫폼에서 대용량 이미지/동영상 파일을 안정적으로 저장 가능                             |
| **설정 관리** | AWS Parameter Store | 환경별 설정 관리 | 개발/스테이징/프로덕션 환경별 데이터베이스 연결 정보, API 키 등 민감한 설정을 안전하게 중앙 관리하고 암호화된 상태로 저장            |
| **컨테이너** | Docker | 애플리케이션 컨테이너화 | 개발 환경과 프로덕션 환경의 일관성을 보장하고, 마이크로서비스 아키텍처로의 확장과 CI/CD 파이프라인 구축을 위한 기반 제공             |
| **API 문서** | Swagger | REST API 문서화 | 프론트엔드 팀과의 협업 효율성을 높이고, API 명세를 자동으로 생성하여 개발 속도를 향상시키며 API 테스트 환경 제공                |
| **코드 생성** | Lombok | 보일러플레이트 코드 자동 생성 | Entity, DTO 클래스의 getter/setter, builder 패턴 등 반복적인 코드를 자동 생성하여 코드 가독성을 높이고 개발 시간 단축 |
| **빌드 도구** | Gradle | 의존성 관리 및 빌드 | Maven보다 빠른 빌드 속도와 유연한 설정을 제공하며, 멀티 모듈 프로젝트 관리와 다양한 플러그인을 통한 확장성 제공                 |
| **언어** | Java 17 | 프로그래밍 언어 | LTS 버전으로 안정성을 보장                                             |

## 프로젝트 구조

```
src/main/java/org/example/gridgestagram/
├── GridgestagramApplication.java          # 메인 애플리케이션 클래스
├── annotation/                            # 커스텀 어노테이션
│   ├── LogAction.java                     # 로그 액션 어노테이션
│   └── LoggingAspect.java                 # AOP 로깅 관점
├── config/                                # 설정 클래스들
│   ├── AsyncConfig.java                   # 비동기 처리 설정
│   ├── CacheConfig.java                   # 캐시 설정
│   ├── CorsConfig.java                    # CORS 설정
│   ├── JacksonDateTimeConfig.java         # 날짜/시간 직렬화 설정
│   ├── PasswordEncoderConfig.java         # 비밀번호 암호화 설정
│   ├── QuerydslConfig.java                # QueryDSL 설정
│   ├── RedisConfig.java                   # Redis 설정
│   ├── S3Config.java                      # AWS S3 설정
│   ├── SchedulingConfig.java              # 스케줄링 설정
│   ├── SecurityConfig.java                # Spring Security 설정
│   ├── SwaggerConfig.java                 # API 문서화 설정
│   └── WebConfig.java                     # 웹 관련 설정
├── controller/                            # REST API 컨트롤러
│   ├── admin/                             # 관리자 기능
│   ├── auth/                              # 인증 관련
│   ├── feed/                              # 피드 관련
│   ├── file/                              # 파일 관련
│   ├── subscription/                      # 구독 관련
│   └── user/                              # 사용자 관련
├── data/                                  # 데이터 관련 클래스
├── exceptions/                            # 커스텀 예외 클래스
├── repository/                            # 데이터 접근 계층
│   ├── feed/                              # 피드 리포지토리
│   ├── files/                             # 파일 리포지토리
│   ├── log/                               # 로그 리포지토리
│   ├── payment/                           # 결제 리포지토리
│   ├── refreshToken/                      # 리프레시 토큰 리포지토리
│   ├── subscription/                      # 구독 리포지토리
│   ├── term/                              # 약관 리포지토리
│   └── user/                              # 사용자 리포지토리
├── security/                              # 보안 관련 클래스
├── service/                               # 비즈니스 로직 계층
│   ├── domain/                            # 도메인 서비스
│   └── facade/                            # 파사드 패턴 서비스
└── utils/                                 # 유틸리티 클래스
```

## 주요 기능

- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **피드 관리**: 게시글 작성, 조회, 수정, 삭제
- **파일 관리**: 이미지/동영상 업로드 및 저장
- **관리자 기능**: 사용자 관리, 피드 관리, 신고 처리
- **인증/인가**: JWT 토큰 기반 보안
- **API 문서화**: Swagger를 통한 API 문서 제공

## 설정 파일

- `application.yml` - 기본 설정
- `application-local.yml` - 로컬 환경 설정
- `application-prod.yml` - 프로덕션 환경 설정