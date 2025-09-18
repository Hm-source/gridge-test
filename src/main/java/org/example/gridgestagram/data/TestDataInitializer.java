package org.example.gridgestagram.data;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.repository.subscription.entity.vo.SubscriptionStatus;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.Role;
import org.example.gridgestagram.repository.user.entity.vo.UserStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class TestDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 필요

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 어드민 사용자
        if (!userRepository.findByUsername("admin").isPresent()) {
            User adminUser = User.builder()
                .username("admin")
                .name("관리자")
                .password(passwordEncoder.encode("admin1234")) // 임시 비번
                .phone("01011112222")
                .provider(null)
                .providerId(null)
                .status(UserStatus.ACTIVE)
                .subscriptionStatus(SubscriptionStatus.INACTIVE)
                .birthdate(LocalDate.of(1980, 1, 1))
                .profileImageUrl("")
                .role(Role.ADMIN)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

            userRepository.save(adminUser);
            log.info("어드민 사용자 생성됨: {}", adminUser.getUsername());
        }
    }
}
