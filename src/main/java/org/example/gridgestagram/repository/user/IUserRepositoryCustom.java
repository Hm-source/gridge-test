package org.example.gridgestagram.repository.user;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.user.entity.User;

public interface IUserRepositoryCustom {

    List<User> findByName(String name);

    Optional<User> findByProviderId(String providerId);
}
