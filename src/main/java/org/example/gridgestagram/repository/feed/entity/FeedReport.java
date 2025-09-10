package org.example.gridgestagram.repository.feed.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.feed.entity.vo.ReportReason;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "feed_report", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"feed_id", "reporter_id"})
})
public class FeedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    public static FeedReport create(Feed feed, User reporter, ReportReason reason,
        String description) {
        return FeedReport.builder()
            .feed(feed)
            .reporter(reporter)
            .reason(reason)
            .description(description)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void approve(User admin) {
        this.status = ReportStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
        this.processedBy = admin;
    }
}
