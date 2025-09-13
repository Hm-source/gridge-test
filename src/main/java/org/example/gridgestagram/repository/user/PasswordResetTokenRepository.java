package org.example.gridgestagram.repository.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.user.entity.PasswordResetToken;
import org.example.gridgestagram.repository.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndIsUsedFalse(String token);

    List<PasswordResetToken> findByUserAndIsUsedFalse(User user);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    @Query("SELECT p FROM PasswordResetToken p WHERE p.user = :user AND p.isUsed = false AND p.expiresAt > :now")
    List<PasswordResetToken> findValidTokensByUser(@Param("user") User user,
        @Param("now") LocalDateTime now);
}
