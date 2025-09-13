package org.example.gridgestagram.service.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2User attributes: {}", attributes);

        String providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String profileImageUrl = (String) profile.get("profile_image_url");

        log.info("nickname: {}, profileImageUrl: {}", nickname, profileImageUrl);

        // 기존 사용자 확인
        Optional<User> existingUser = userRepository.findByProviderId(providerId);

        if (existingUser.isPresent()) {
            // 기존 사용자 - 바로 OAuth2User 반환
            log.info("기존 사용자 발견: {}", existingUser.get().getUsername());
            User user = existingUser.get();

            return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "id"
            );
        } else {
            // 신규 사용자 - DB에 저장하지 않고 attributes에 신규 표시 추가
            log.info("신규 사용자 - 회원가입 페이지로 이동 예정");

            Map<String, Object> modifiedAttributes = new HashMap<>(attributes);
            modifiedAttributes.put("isNewUser", true);
            modifiedAttributes.put("extractedNickname", nickname);
            modifiedAttributes.put("extractedProfileImage", profileImageUrl);

            return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_TEMP_USER")),
                modifiedAttributes,
                "id"
            );
        }
    }
}

