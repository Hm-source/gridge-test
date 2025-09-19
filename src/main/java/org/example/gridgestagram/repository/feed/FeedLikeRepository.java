package org.example.gridgestagram.repository.feed;

import java.util.List;
import java.util.Optional;
import org.example.gridgestagram.repository.feed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByFeedIdAndUserId(Long feedId, Long userId);

    boolean existsByFeedIdAndUserId(Long feedId, Long userId);

    void deleteByFeedIdAndUserId(Long feedId, Long userId);

    @Query(value = "SELECT * FROM feed_like fl WHERE fl.feed_id = :feedId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<FeedLike> findRandomLikes(@Param("feedId") Long feedId, @Param("limit") int limit);

}
