package org.example.gridgestagram.annotation;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.service.domain.AdminLogService;
import org.example.gridgestagram.service.domain.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final AdminLogService adminLogService;
    private final AuthenticationService authenticationService;

    @Around("@annotation(logAction)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, LogAction logAction)
        throws Throwable {

        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null) {
            return joinPoint.proceed();
        }

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

    private void recordSuccessLog(LogAction logAction, User user, String methodName,
        Object[] args, Object result) {
        try {
            String description = buildDescription(logAction, args);
            Long targetId = extractTargetId(args, result);

            if (logAction.async()) {
                adminLogService.createLogAsync(
                    logAction.value(), user, logAction.targetType(), targetId, description);
            } else {
                adminLogService.createLogWithTarget(
                    logAction.value(), user.getId(), user.getRole(), logAction.targetType(),
                    targetId, description);
            }

        } catch (Exception e) {
            log.warn("성공 로그 기록 실패: {}", e.getMessage());
        }
    }

    private void recordFailureLog(LogAction logAction, User user, String methodName,
        Object[] args, Exception error) {
        try {
            String description = buildDescription(logAction, args)
                + " [실패: " + error.getMessage() + "]";

            adminLogService.createLogAsync(
                logAction.value(), user, "", null, description);

        } catch (Exception e) {
            log.warn("실패 로그 기록 실패: {}", e.getMessage());
        }
    }

    private String buildDescription(LogAction logAction, Object[] args) {
        if (StringUtils.hasText(logAction.description())) {
            return logAction.description();
        }
        StringBuilder sb = new StringBuilder(logAction.value().getDescription());
        for (Object arg : args) {
            if (arg instanceof String str) {
                if (str.length() < 100) {
                    sb.append(" - ").append(str);
                    break;
                }
            }
        }

        return sb.toString();
    }

    private Long extractTargetId(Object[] args, Object result) {
        for (Object arg : args) {
            if (arg instanceof Long && (Long) arg > 0) {
                return (Long) arg;
            }
        }

        if (result != null) {
            try {
                Method getIdMethod = result.getClass().getMethod("getId");
                Object id = getIdMethod.invoke(result);
                if (id instanceof Long) {
                    return (Long) id;
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}
