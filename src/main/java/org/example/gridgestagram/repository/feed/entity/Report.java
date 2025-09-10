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
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;
import org.example.gridgestagram.repository.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "reports", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"type", "target_id", "reporter_id"})
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportType type;   // FEED or COMMENT

    @Column(name = "target_id", nullable = false)
    private Long targetId;     // FeedId or CommentId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    public static Report create(ReportType type, Long targetId, User reporter,
        ReportReason reason) {
        return Report.builder()
            .type(type)
            .targetId(targetId)
            .reporter(reporter)
            .reason(reason)
            .createdAt(LocalDateTime.now())
            .build();
    }
    

    public void approve(User admin) {
        this.status = ReportStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
        this.processedBy = admin;
    }
}
