package org.example.gridgestagram.repository.feed;

import java.util.List;
import org.example.gridgestagram.repository.feed.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c.* FROM comment c " +
        "JOIN users u ON c.user_id = u.id " +
        "WHERE c.feed_id = :feedId " +
        "ORDER BY c.created_at DESC " +
        "LIMIT 3",
        nativeQuery = true)
    List<Comment> findTopCommentsByFeedId(@Param("feedId") Long feedId, int limit);

    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByFeedIdOrderByCreatedAtDesc(Long feedId, Pageable pageable);

    long countByFeedId(Long feedId);

}
