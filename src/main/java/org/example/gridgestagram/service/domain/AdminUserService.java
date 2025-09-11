package org.example.gridgestagram.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.admin.dto.UserDetailResponse;
import org.example.gridgestagram.controller.admin.dto.UserSearchCondition;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserDetailResponse> searchUsers(UserSearchCondition condition, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(condition, pageable);

        return users.map(UserDetailResponse::from);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserDetailResponse.from(user);
    }
}
