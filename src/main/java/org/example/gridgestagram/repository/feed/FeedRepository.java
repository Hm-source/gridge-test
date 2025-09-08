package org.example.gridgestagram.repository.feed;

import java.util.Optional;
import org.example.gridgestagram.repository.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

    @Query(value = "SELECT DISTINCT f FROM Feed f " +
        "LEFT JOIN FETCH f.user " +
        "LEFT JOIN FETCH f.files " +
        "WHERE f.isVisible = true " +
        "ORDER BY f.createdAt DESC",
        countQuery = "SELECT COUNT(f) FROM Feed f WHERE f.isVisible = true")
    Page<Feed> findByIsVisibleTrue(Pageable pageable);

    Optional<Feed> findByIdAndIsVisibleTrue(Long id);

    Page<Feed> findAll(Pageable pageable);

    Optional<Feed> findByIdAndUserId(Long feedId, Long userId);
}
