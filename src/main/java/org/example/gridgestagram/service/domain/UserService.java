package org.example.gridgestagram.service.domain;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.auth.dto.SignUpRequest;
import org.example.gridgestagram.controller.auth.dto.SignUpResponse;
import org.example.gridgestagram.controller.auth.dto.TermsAgreementRequest;
import org.example.gridgestagram.controller.user.dto.OAuth2SignUpRequest;
import org.example.gridgestagram.controller.user.dto.OAuth2UserInfo;
import org.example.gridgestagram.controller.user.dto.UserResponse;
import org.example.gridgestagram.repository.term.UserTermsRepository;
import org.example.gridgestagram.repository.term.entity.Terms;
import org.example.gridgestagram.repository.term.entity.UserTerms;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.Provider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserTermsRepository userTermsRepository;
    private final TermsService termsService;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("유저가 데이터베이스 내 존재하지 않습니다. 유저 id : " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("유저가 데이터베이스 내 존재하지 않습니다. 유저 id : " + username));
    }

    
    @Transactional(readOnly = true)
    public User findByProviderId(String id) {
        return userRepository.findByProviderId(id)
            .orElseThrow(() -> new RuntimeException("유저가 데이터베이스 내 존재하지 않습니다. 유저 id : " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("존재하지 않는 유저입니다 - username : " + username));
        return user;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findByName(String username) {
        List<User> users = userRepository.findByName(username);
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
            .stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional
    public SignUpResponse save(SignUpRequest request, String url) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.createBasicUser(
            request.getUsername(),
            request.getName(),
            encodedPassword,
            request.getPhone(),
            request.getBirthdate(),
            url
        );

        validateTermsAgreements(request.getTermsAgreement());
        User created = userRepository.save(user);
        processTermsAgreements(created, request.getTermsAgreement());

        return SignUpResponse.from(created);
    }

    @Transactional
    public SignUpResponse saveOAuth2User(OAuth2SignUpRequest request) {
        // 카카오 정보 디코딩
        OAuth2UserInfo userInfo = decodeOAuth2Info(request.getKakaoInfo());

        // 중복 확인
        validateProviderIdNotExists(userInfo.getProviderId());

        // 약관 동의 검증
        validateTermsAgreements(request.getTermsAgreement());

        // OAuth2 사용자 생성
        User user = User.createSocialUser(
            userInfo.getName(),
            userInfo.getName(),
            request.getPhone(),
            Provider.KAKAO,
            userInfo.getProviderId(),
            request.getBirthdate(),
            userInfo.getProfileImageUrl() != null ? userInfo.getProfileImageUrl() : ""
        );

        User created = userRepository.save(user);
        processTermsAgreements(created, request.getTermsAgreement());

        return SignUpResponse.from(created);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public void validateUsernameNotExists(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 username 입니다: " + username);
        }
    }

    private OAuth2UserInfo decodeOAuth2Info(String encodedInfo) {
        try {
            String decodedInfo = new String(
                Base64.getDecoder().decode(encodedInfo),
                StandardCharsets.UTF_8
            );
            String[] infoParts = decodedInfo.split("\\|");

            if (infoParts.length != 4) {
                throw new IllegalArgumentException("잘못된 OAuth2 사용자 정보 형식입니다.");
            }

            return OAuth2UserInfo.builder()
                .providerId(infoParts[0])
                .name(infoParts[2])
                .profileImageUrl(infoParts[3].isEmpty() ? null : infoParts[3])
                .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("OAuth2 사용자 정보 디코딩에 실패했습니다.");
        }
    }
}
