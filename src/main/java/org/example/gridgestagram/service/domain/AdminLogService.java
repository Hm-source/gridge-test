package org.example.gridgestagram.service.domain;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.admin.dto.AdminLogResponse;
import org.example.gridgestagram.controller.admin.dto.AdminLogSearchCondition;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.log.AdminLogRepository;
import org.example.gridgestagram.repository.log.entity.AdminLog;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.UserRepository;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createLogWithTarget(LogType logType, Long userId, Role role, String targetType,
        Long targetId, String description) {
        AdminLog log = AdminLog.createWithTarget(logType, userId, role, targetType, targetId,
            description);
        adminLogRepository.save(log);
    }

    public Page<AdminLogResponse> searchLogs(AdminLogSearchCondition condition, Pageable pageable) {
        try {
            Page<AdminLog> logPage = adminLogRepository.searchLogs(condition, pageable);
            return logPage.map(log -> {
                String username = getUsername(log.getUserId());
                String name = getUserName(log.getUserId());
                Role role = getRole(log.getUserId());
                return AdminLogResponse.fromWithUserInfo(log, username, name,
                    role);
            });
        } catch (Exception e) {
            log.error("로그 검색 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.LOG_SEARCH_FAILED);
        }
    }


    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
            .map(User::getUsername)
            .orElse("탈퇴한 사용자");
    }

    private String getUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
            .map(User::getName)
            .orElse("탈퇴한 사용자");
    }

    private Role getRole(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
            .map(User::getRole)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Async("logExecutor")
    @Transactional
    public CompletableFuture<Void> createLogAsync(LogType logType, User user, String targetType,
        Long targetId, String description) {
        try {
            AdminLog log;
            if (StringUtils.hasText(targetType) && targetId != null) {
                log = AdminLog.createWithTarget(logType, user.getId(), user.getRole(), targetType,
                    targetId,
                    description);
            } else {
                log = AdminLog.create(logType, user.getId(), user.getRole(), description);
            }
            adminLogRepository.save(log);

        } catch (Exception e) {
            log.error("비동기 로그 생성 실패: userId={}, logType={}", user.getId(), logType, e);
        }
        return CompletableFuture.completedFuture(null);
    }

}
