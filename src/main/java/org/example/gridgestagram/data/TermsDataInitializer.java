package org.example.gridgestagram.data;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.repository.term.TermsRepository;
import org.example.gridgestagram.repository.term.entity.Terms;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class TermsDataInitializer implements ApplicationRunner {

    private final TermsRepository termsRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (termsRepository.count() == 0) {
            initializeTermsData();
            log.info("Terms 초기 데이터 생성 완료");
        }
    }

    private void initializeTermsData() {

        // 2. 이용약관 (필수)
        Terms serviceTerms = Terms.builder()
            .title("이용약관 (필수)")
            .content(getServiceTermsContent())
            .isRequired(true)
            .createdAt(LocalDateTime.now())
            .build();
        termsRepository.save(serviceTerms);

        // 3. 데이터 정책 (필수)
        Terms dataPolicy = Terms.builder()
            .title("데이터 정책 (필수)")
            .content(getDataPolicyContent())
            .isRequired(true)
            .createdAt(LocalDateTime.now())
            .build();
        termsRepository.save(dataPolicy);

        // 4. 위치 기반 기능 (필수)
        Terms locationService = Terms.builder()
            .title("위치 기반 기능 (필수)")
            .content(getLocationServiceContent())
            .isRequired(true)
            .createdAt(LocalDateTime.now())
            .build();
        termsRepository.save(locationService);

    }

    private String getServiceTermsContent() {
        return """
            제1조 (목적)
            이 약관은 Tnovel(이하 "회사")이 제공하는 서비스의 이용조건 및 절차, 회사와 회원간의 권리·의무 및 책임사항을 규정함을 목적으로 합니다.
            
            제2조 (정의)
            1. "서비스"라 함은 회사가 제공하는 모든 서비스를 의미합니다.
            2. "회원"이라 함은 이 약관에 동의하고 회사와 서비스 이용계약을 체결한 개인을 말합니다.
            
            제3조 (약관의 게시와 개정)
            1. 회사는 이 약관의 내용을 회원이 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다.
            2. 회사는 필요한 경우 이 약관을 개정할 수 있습니다.
            
            제4조 (서비스의 제공 및 변경)
            1. 회사는 다음과 같은 업무를 수행합니다.
               - 소설 콘텐츠 제공 서비스
               - 사용자 맞춤형 추천 서비스
               - 기타 회사가 정하는 업무
            """;
    }

    private String getDataPolicyContent() {
        return """
            개인정보 수집 및 이용에 대한 동의
            
            1. 수집하는 개인정보 항목
            - 필수항목: 이름, 이메일주소, 휴대폰번호, 생년월일
            - 선택항목: 프로필 사진, 관심 장르
            
            2. 개인정보 수집 및 이용목적
            - 회원 가입 및 관리
            - 서비스 제공 및 개선
            - 고객상담 및 민원처리
            - 법령 및 약관 위반 회원에 대한 이용제한 조치
            
            3. 개인정보 보유 및 이용기간
            - 회원탈퇴 시까지 (단, 법령에서 정한 경우 해당 기간까지 보관)
            
            4. 개인정보 수집 동의 거부 시
            - 필수항목 수집 동의를 거부하시는 경우 서비스 이용이 제한됩니다.
            """;
    }

    private String getLocationServiceContent() {
        return """
            위치기반서비스 이용약관
            
            제1조 (목적)
            이 약관은 회사가 제공하는 위치기반서비스에 대해 회사와 개인위치정보주체와의 권리·의무 및 책임사항을 규정함을 목적으로 합니다.
            
            제2조 (서비스 내용)
            회사는 GPS칩이 내장된 스마트폰을 통해 수집된 위치정보를 이용하여 다음과 같은 위치기반서비스를 제공합니다:
            - 현재 위치 기반 맞춤 콘텐츠 제공
            - 지역별 이벤트 정보 제공
            - 근처 서점 및 문화시설 정보 제공
            
            제3조 (개인위치정보의 수집)
            1. 회사는 개인위치정보주체의 동의를 얻어 개인위치정보를 수집합니다.
            2. 개인위치정보는 해당 서비스 제공을 위해서만 이용됩니다.
            """;
    }

}
