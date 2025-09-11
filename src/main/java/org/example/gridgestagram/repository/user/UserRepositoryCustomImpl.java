package org.example.gridgestagram.repository.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.UserSearchCondition;
import org.example.gridgestagram.repository.term.entity.QTerms;
import org.example.gridgestagram.repository.term.entity.QUserTerms;
import org.example.gridgestagram.repository.user.entity.QUser;
import org.example.gridgestagram.repository.user.entity.User;
import org.example.gridgestagram.repository.user.entity.vo.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findByName(String name) {
        return jpaQueryFactory.selectFrom(QUser.user)
            .where(QUser.user.name.trim().eq(name))
            .fetch();
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return Optional.ofNullable(
            jpaQueryFactory.selectFrom(QUser.user)
                .where((QUser.user.providerId.eq(providerId)))
                .fetchOne()
        );
    }

    @Override
    public Page<User> searchUsers(UserSearchCondition condition, Pageable pageable) {
        QUser user = QUser.user;
        QUserTerms userTerms = QUserTerms.userTerms;
        QTerms terms = QTerms.terms;

        JPAQuery<User> query = jpaQueryFactory
            .selectFrom(user)
            .distinct();

        BooleanBuilder whereClause = buildWhereClause(condition, user, userTerms, terms);
        query.where(whereClause);

        if (needsTermsJoin(condition)) {
            query.leftJoin(userTerms).on(userTerms.user.eq(user))
                .leftJoin(terms).on(userTerms.terms.eq(terms));
        }

        query.orderBy(getOrderSpecifier(condition, user));

        List<User> users = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = jpaQueryFactory
            .select(user.count())
            .from(user)
            .where(whereClause)
            .fetch().size();

        return new PageImpl<>(users, pageable, total);
    }

    private BooleanBuilder buildWhereClause(UserSearchCondition condition, QUser user,
        QUserTerms userTerms, QTerms terms) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(condition.getUsername())) {
            builder.and(user.username.containsIgnoreCase(condition.getUsername()));
        }

        if (StringUtils.hasText(condition.getName())) {
            builder.and(user.name.eq(condition.getName()));
        }

        if (StringUtils.hasText(condition.getPhone())) {
            builder.and(user.phone.eq(condition.getPhone()));
        }

        if (condition.getStatus() != null) {
            builder.and(user.status.eq(condition.getStatus()));
        }

        if (condition.getStatusList() != null && !condition.getStatusList().isEmpty()) {
            builder.and(user.status.in(condition.getStatusList()));
        }

        if (condition.getJoinDateFrom() != null) {
            builder.and(user.createdAt.goe(condition.getJoinDateFrom().atStartOfDay()));
        }
        if (condition.getJoinDateTo() != null) {
            builder.and(user.createdAt.loe(condition.getJoinDateTo().atTime(23, 59, 59)));
        }

        if (condition.getLastLoginFrom() != null) {
            builder.and(user.lastLoginAt.goe(condition.getLastLoginFrom().atStartOfDay()));
        }
        if (condition.getLastLoginTo() != null) {
            builder.and(user.lastLoginAt.loe(condition.getLastLoginTo().atTime(23, 59, 59)));
        }

        if (condition.getIsDormant() != null) {
            if (condition.getIsDormant()) {
                builder.and(user.status.eq(UserStatus.DORMANT));
            } else {
                builder.and(user.status.ne(UserStatus.DORMANT));
            }
        }

        if (condition.getDormantDays() != null) {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(condition.getDormantDays());
            builder.and(user.lastLoginAt.lt(cutoffDate)
                .or(user.lastLoginAt.isNull()));
        }

        if (condition.getProvider() != null) {
            builder.and(user.provider.eq(condition.getProvider()));
        }

        if (condition.getRole() != null) {
            builder.and(user.role.eq(condition.getRole()));
        }

        if (condition.getSubscriptionStatus() != null) {
            builder.and(user.subscriptionStatus.eq(condition.getSubscriptionStatus()));
        }

        return builder;
    }

    private boolean needsTermsJoin(UserSearchCondition condition) {
        return condition.getHasExpiredTerms() != null || condition.getTermsId() != null;
    }

    private OrderSpecifier<?> getOrderSpecifier(UserSearchCondition condition, QUser user) {
        String sortBy = condition.getSortBy();
        boolean isAsc = "asc".equalsIgnoreCase(condition.getSortDirection());

        return switch (sortBy) {
            case "username" -> isAsc ? user.username.asc() : user.username.desc();
            case "createdAt" -> isAsc ? user.createdAt.asc() : user.createdAt.desc();
            case "lastLoginAt" -> isAsc ? user.lastLoginAt.asc() : user.lastLoginAt.desc();
            case "status" -> isAsc ? user.status.asc() : user.status.desc();
            default -> user.createdAt.desc();
        };
    }


}
