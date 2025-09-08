package org.example.gridgestagram.repository.files.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.feed.entity.Feed;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "files")
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "order_seq", nullable = false)
    private Integer order;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Files create(Feed feed, String url, Integer order) {
        return Files.builder()
            .feed(feed)
            .url(url)
            .order(order)
            .createdAt(LocalDateTime.now())
            .build();
    }
}