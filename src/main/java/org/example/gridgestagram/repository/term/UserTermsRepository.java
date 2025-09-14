package org.example.gridgestagram.repository.term;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.example.gridgestagram.repository.term.entity.UserTerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTermsRepository extends JpaRepository<UserTerms, Long> {

    // 재동의가 필요한 약관들 조회 (만료일이 지난 것들)
    @Query("SELECT ut FROM UserTerms ut WHERE ut.isAgreed = true "
        + "AND ut.nextAgreedDate < :currentDate")
    List<UserTerms> findExpiredAgreements(@Param("currentDate") LocalDateTime currentDate);

    List<UserTerms> findByTermsIdAndNextAgreedDateAndIsAgreed(Long termsId,
        LocalDate nextAgreedDate,
        boolean isAgreed);
}
