package org.example.gridgestagram.repository.user;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.user.entity.PasswordResetToken;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndIsUsedFalse(String token);

    List<PasswordResetToken> findByUserAndIsUsedFalse(User user);

}
