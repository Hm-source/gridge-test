package org.example.gridgestagram.repository.feed.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Builder.Default
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Builder.Default
    @Column(name = "report_count", nullable = false)
    private Integer reportCount = 0;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "feed", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "feed", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "feed", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<FeedReport> reports = new ArrayList<>();


    public static Feed create(User user, String content) {
        return Feed.builder()
            .user(user)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void hide() {
        this.isVisible = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void incrementReportCount() {
        this.reportCount++;
        // 신고 횟수가 임계값을 넘으면 자동 숨김 (임시로 처리)
        if (this.reportCount >= 5) {
            hide();
        }
    }
}
