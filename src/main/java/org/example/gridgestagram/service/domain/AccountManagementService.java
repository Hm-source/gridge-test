package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountManagementService {

    private final UserRepository userRepository;

    @Transactional
    public void suspendAccount(Long userId) {
        User user = findUserById(userId);

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_SUSPENDED);
        }

        user.suspend();
        userRepository.save(user);

        log.info("계정 일시정지 처리 완료 - 사용자: {}", user.getUsername());
    }

    @Transactional
    public void unsuspendAccount(Long userId) {
        User user = findUserById(userId);

        if (user.getStatus() != UserStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_SUSPENDED);
        }

        user.unsuspend();
        userRepository.save(user);

        log.info("계정 일시정지 해제 완료 - 사용자: {}", user.getUsername());
    }

    @Transactional
    public void activateDormantAccount(Long userId) {
        User user = findUserById(userId);

        if (user.getStatus() != UserStatus.DORMANT) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_DORMANT);
        }

        user.activateFromDormant();
        userRepository.save(user);

        log.info("휴면 계정 활성화 완료 - 사용자: {}", user.getUsername());
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


}
