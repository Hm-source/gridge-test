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
    Page<Comment> findByFeedIdAndIsVisibleTrueOrderByCreatedAtDesc(Long feedId, Pageable pageable);

    long countByFeedId(Long feedId);

    @Query(value = """
        SELECT c.* FROM comment c
        INNER JOIN users u ON c.user_id = u.id
        INNER JOIN (
            SELECT c2.feed_id, c2.id,
                   ROW_NUMBER() OVER (PARTITION BY c2.feed_id ORDER BY c2.created_at DESC) as rn
            FROM comment c2
            WHERE c2.feed_id IN :feedIds AND c2.is_visible = true
        ) ranked ON c.id = ranked.id AND ranked.rn <= 3
        ORDER BY c.feed_id, c.created_at DESC
        """, nativeQuery = true)
    List<Comment> findTop3CommentsByFeedIds(@Param("feedIds") List<Long> feedIds);
}
