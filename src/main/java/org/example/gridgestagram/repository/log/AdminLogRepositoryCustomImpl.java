package org.example.gridgestagram.repository.log;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.AdminLogSearchCondition;
import org.example.gridgestagram.repository.log.entity.AdminLog;
import org.example.gridgestagram.repository.log.entity.QAdminLog;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.entity.QUser;
import org.example.gridgestagram.repository.user.entity.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class AdminLogRepositoryCustomImpl implements AdminLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QAdminLog adminLog = QAdminLog.adminLog;
    QUser user = QUser.user;

    public Page<AdminLog> searchLogs(AdminLogSearchCondition condition, Pageable pageable) {
        List<AdminLog> logs = queryFactory
            .selectFrom(adminLog)
            .where(
                logTypeEq(condition.getLogType()),
                categoryEq(condition.getCategory()),
                userIdEq(condition.getUserId()),
                roleEq(condition.getRole()),
                targetTypeEq(condition.getTargetType()),
                targetIdEq(condition.getTargetId()),
                descriptionContains(condition.getDescription()),
                createdAtBetween(condition.getStartDate(), condition.getEndDate())
            )
            .orderBy(getOrderSpecifier(condition.getOrderBy(), condition.getDirection()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(adminLog)
            .where(
                logTypeEq(condition.getLogType()),
                categoryEq(condition.getCategory()),
                userIdEq(condition.getUserId()),
                roleEq(condition.getRole()),
                targetTypeEq(condition.getTargetType()),
                targetIdEq(condition.getTargetId()),
                descriptionContains(condition.getDescription()),
                createdAtBetween(condition.getStartDate(), condition.getEndDate())
            )
            .fetchCount();

        return new PageImpl<>(logs, pageable, total);
    }


    public List<AdminLog> findRecentLogs(int limit) {
        return queryFactory
            .selectFrom(adminLog)
            .orderBy(adminLog.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    public List<AdminLog> findLogsByUser(Long userId, int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        return queryFactory
            .selectFrom(adminLog)
            .where(
                adminLog.userId.eq(userId),
                adminLog.createdAt.goe(since)
            )
            .orderBy(adminLog.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    private BooleanExpression logTypeEq(LogType logType) {
        return logType != null ? adminLog.logType.eq(logType) : null;
    }

    private BooleanExpression categoryEq(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }

        List<LogType> logTypes = Arrays.stream(LogType.values())
            .filter(type -> type.getCategory().equals(category))
            .collect(Collectors.toList());

        return adminLog.logType.in(logTypes);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? adminLog.userId.eq(userId) : null;
    }

    private BooleanExpression roleEq(Role role) {
        return role != null ? adminLog.role.eq(role) : null;
    }

    private BooleanExpression targetTypeEq(String targetType) {
        return StringUtils.hasText(targetType) ? adminLog.targetType.eq(targetType) : null;
    }

    private BooleanExpression targetIdEq(Long targetId) {
        return targetId != null ? adminLog.targetId.eq(targetId) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? adminLog.description.containsIgnoreCase(
            description) : null;
    }


    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return adminLog.createdAt.between(start, end);
        } else if (start != null) {
            return adminLog.createdAt.goe(start);
        } else if (end != null) {
            return adminLog.createdAt.loe(end);
        }
        return null;
    }

    private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
        Order order = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        switch (orderBy.toLowerCase()) {
            case "logtype":
                return new OrderSpecifier<>(order, adminLog.logType);
            case "userid":
                return new OrderSpecifier<>(order, adminLog.userId);
            default:
                return new OrderSpecifier<>(order, adminLog.createdAt);
        }
    }
}
