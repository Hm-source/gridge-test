package org.example.gridgestagram.repository.term.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_agreed", nullable = false)
    private Boolean isAgreed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "next_agreed_date")
    private LocalDate nextAgreedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    public static UserTerms create(User user, Terms terms, Boolean isAgreed) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        return UserTerms.builder()
            .user(user)
            .terms(terms)
            .isAgreed(isAgreed)
            .createdAt(now)
            .nextAgreedDate(calculateNextAgreedDate(today, isAgreed))
            .build();
    }

    private static LocalDate calculateNextAgreedDate(LocalDate today,
        Boolean isAgreed) {
        if (isAgreed == null || !isAgreed) {
            return null;
        }

        return today.plusYears(1);
    }

    public void renewAgreement() {
        this.isAgreed = true;
        this.updatedAt = LocalDateTime.now();
        this.nextAgreedDate = LocalDate.from(this.updatedAt.plusYears(1));
    }

    public void revokeAgreement() {
        this.isAgreed = false;
        this.updatedAt = LocalDateTime.now();
        this.nextAgreedDate = null;
    }
}
