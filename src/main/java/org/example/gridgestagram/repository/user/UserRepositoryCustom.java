package org.example.gridgestagram.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.repository.user.entity.QUser;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustom implements IUserRepositoryCustom {

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


}
