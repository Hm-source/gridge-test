package org.example.gridgestagram.repository.feed;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.AdminFeedSearchCondition;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.example.gridgestagram.repository.feed.entity.QFeed;
import org.example.gridgestagram.repository.feed.entity.vo.FeedStatus;
import org.example.gridgestagram.repository.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Repository
public class FeedRepositoryImpl implements AdminFeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QFeed feed = QFeed.feed;
    QUser user = QUser.user;

    @Override
    public Page<Feed> searchFeeds(AdminFeedSearchCondition condition, Pageable pageable) {
        List<Feed> feeds = queryFactory
            .selectFrom(feed)
            .leftJoin(feed.user, user).fetchJoin()
            .where(
                userIdEq(condition.getUserId()),
                usernameContains(condition.getUsername()),
                phoneContains(condition.getPhone()),
                contentContains(condition.getContent()),
                statusEq(condition.getStatus()),
                createdAtBetween(condition.getStartDate(), condition.getEndDate())
            )
            .orderBy(getOrderSpecifier(condition.getOrderBy(), condition.getDirection()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(feed)
            .leftJoin(feed.user, user)
            .where(
                userIdEq(condition.getUserId()),
                usernameContains(condition.getUsername()),
                phoneContains(condition.getPhone()),
                contentContains(condition.getContent()),
                statusEq(condition.getStatus()),
                createdAtBetween(condition.getStartDate(), condition.getEndDate())
            )
            .fetchCount();

        return new PageImpl<>(feeds, pageable, total);
    }


    public List<Feed> findFeedsByUser(Long userId, int limit) {
        return queryFactory
            .selectFrom(feed)
            .leftJoin(feed.user, user).fetchJoin()
            .where(feed.user.id.eq(userId))
            .orderBy(feed.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    public List<Feed> findRecentFeeds(int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        return queryFactory
            .selectFrom(feed)
            .leftJoin(feed.user, user).fetchJoin()
            .where(feed.createdAt.goe(since))
            .orderBy(feed.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? feed.user.id.eq(userId) : null;
    }

    private BooleanExpression usernameContains(String username) {
        return StringUtils.hasText(username) ? user.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression phoneContains(String phone) {
        return StringUtils.hasText(phone) ? user.phone.containsIgnoreCase(phone) : null;
    }

    private BooleanExpression contentContains(String content) {
        return StringUtils.hasText(content) ? feed.content.containsIgnoreCase(content) : null;
    }

    private BooleanExpression statusEq(FeedStatus status) {
        return status != null ? feed.status.eq(status) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return feed.createdAt.between(start, end);
        } else if (start != null) {
            return feed.createdAt.goe(start);
        } else if (end != null) {
            return feed.createdAt.loe(end);
        }
        return null;
    }


    private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
        Order order = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        switch (orderBy.toLowerCase()) {
            case "updatedat":
                return new OrderSpecifier<>(order, feed.updatedAt);
            default:
                return new OrderSpecifier<>(order, feed.createdAt);
        }
    }
}
