package org.example.gridgestagram.repository.feed;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.feed.entity.FeedLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByFeedIdAndUserId(Long feedId, Long userId);

    long countByFeedId(Long feedId);

    Page<FeedLike> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<FeedLike> findByFeedIdOrderByCreatedAtDesc(Long feedId, Pageable pageable);

    boolean existsByFeedIdAndUserId(Long feedId, Long userId);

    void deleteByFeedIdAndUserId(Long feedId, Long userId);

    List<FeedLike> findByFeedId(Long feedId);

    @Query("SELECT fl FROM FeedLike fl WHERE fl.feed.id IN :feedIds")
    List<FeedLike> findByFeedIds(@Param("feedIds") List<Long> feedIds);
}
