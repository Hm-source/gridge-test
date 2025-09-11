package org.example.gridgestagram.repository.feed.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.feed.entity.vo.FeedStatus;
import org.example.gridgestagram.repository.files.entity.Files;
import org.example.gridgestagram.repository.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Feed {

    private final static int MAX_FILES_COUNT = 10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    //    @Default
//    @Column(name = "is_visible", nullable = false)
//    private Boolean isVisible = true;
    @Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    @Default
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
    @Default
    @Column(name = "report_count", nullable = false)
    private Integer reportCount = 0;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FeedStatus status = FeedStatus.ACTIVE;


    @Builder.Default
    @OneToMany(mappedBy = "feed", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<Files> files = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "feed", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "feed", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    public static Feed create(User user, String content) {
        return Feed.builder()
            .user(user)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void hide() {
        this.status = FeedStatus.HIDDEN;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void addFile(Files file) {
        this.files.add(file);
    }

    public void increaseLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isLikedBy(Long userId) {
        return this.likes.stream()
            .anyMatch(like -> like.getUser().getId().equals(userId));
    }

    public void deleteByUser() {
        this.status = FeedStatus.USER_DELETED;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = "USER";
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteByAdmin(String reason) {
        this.status = FeedStatus.ADMIN_DELETED;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = "ADMIN";
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsReported() {
        this.status = FeedStatus.REPORTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(FeedStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        if (newStatus.isDeleted()) {
            this.deletedAt = LocalDateTime.now();
            this.deletedBy = "ADMIN";
        } else if (newStatus == FeedStatus.ACTIVE) {
            // 복구하는 경우
            this.deletedAt = null;
            this.deletedBy = null;
        }
    }
    
    public boolean isDeleted() {
        return this.status.isDeleted();
    }

    public boolean isActive() {
        return this.status == FeedStatus.ACTIVE;
    }
}
