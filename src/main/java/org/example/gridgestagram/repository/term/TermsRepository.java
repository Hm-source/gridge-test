package org.example.gridgestagram.repository.term;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.term.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

    List<Terms> findByIsRequired(Boolean isRequired);

    List<Terms> findAllByOrderByIsRequiredDescCreatedAtAsc();

    Optional<Terms> findByTitle(String title);

    Optional<Terms> findByTitleContaining(String title);
}
