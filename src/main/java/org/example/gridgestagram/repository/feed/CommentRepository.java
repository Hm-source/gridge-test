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

    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByFeedIdOrderByCreatedAtDesc(Long feedId, Pageable pageable);

    long countByFeedId(Long feedId);
    
    @Query("SELECT c FROM Comment c " +
        "JOIN FETCH c.user " +
        "WHERE c.feed.id IN :feedIds " +
        "AND c.id IN (" +
        "  SELECT c2.id FROM Comment c2 " +
        "  WHERE c2.feed.id = c.feed.id " +
        "  ORDER BY c2.createdAt DESC " +
        "  LIMIT 3" +
        ") " +
        "ORDER BY c.feed.id, c.createdAt DESC")
    List<Comment> findTop3CommentsByFeedIds(@Param("feedIds") List<Long> feedIds);
}
