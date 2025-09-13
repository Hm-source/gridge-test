package org.example.gridgestagram.repository.user;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.controller.admin.dto.UserSearchCondition;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    List<User> findByName(String name);

    Optional<User> findByProviderId(String providerId);

    Page<User> searchUsers(UserSearchCondition condition, Pageable pageable);
}
