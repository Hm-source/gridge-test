package org.example.gridgestagram.repository.feed;

import java.util.List;
import org.example.gridgestagram.controller.feed.dto.LikePair;
import org.example.gridgestagram.controller.feed.dto.UserProjection;
import org.example.gridgestagram.repository.feed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    boolean existsByFeedIdAndUserId(Long feedId, Long userId);

    @Query(value = """
        SELECT u.id as id, u.username as username, u.profile_image_url, 
               u.role as role, u.name as name, u.subscription_status
        FROM feed_like fl 
        JOIN users u ON fl.user_id = u.id 
        WHERE fl.feed_id = :feedId 
        ORDER BY RAND() 
        LIMIT :limit
        """, nativeQuery = true)
    List<UserProjection> findRandomLikeUsersNative(@Param("feedId") Long feedId,
        @Param("limit") int limit);

    @Query("SELECT (fl.feedId, fl.userId) " +
        "FROM FeedLike fl WHERE fl.feedId IN :feedIds AND fl.userId IN :userIds")
    List<LikePair> findExistingPairs(@Param("feedIds") List<Long> feedIds,
        @Param("userIds") List<Long> userIds);

    @Modifying
    @Query("DELETE FROM FeedLike fl WHERE fl.feedId IN :feedIds AND fl.userId IN :userIds")
    int batchDeleteByFeedIdsAndUserIds(@Param("feedIds") List<Long> feedIds,
        @Param("userIds") List<Long> userIds);

}
