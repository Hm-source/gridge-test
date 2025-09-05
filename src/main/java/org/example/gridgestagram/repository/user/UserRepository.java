package org.example.gridgestagram.repository.user;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, IUserRepositoryCustom {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    User save(User entity);

    void deleteById(Long id);
}
