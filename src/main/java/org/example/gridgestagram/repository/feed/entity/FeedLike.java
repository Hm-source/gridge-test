package org.example.gridgestagram.repository.feed.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_like",
    uniqueConstraints = @UniqueConstraint(columnNames = {"feed_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class FeedLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feed_id", nullable = false)
    private Long feedId;  // 엔티티 대신 ID만

    @Column(name = "user_id", nullable = false)
    private Long userId;  // 엔티티 대신 ID만

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static FeedLike create(Long feedId, Long userId) {
        return FeedLike.builder()
            .feedId(feedId)
            .userId(userId)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
