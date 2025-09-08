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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.controller.feed.dto.FileUploadInfo;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
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
    @Default
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;
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

    @Default
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

    public void addFiles(List<FileUploadInfo> fileInfos) {
        if (fileInfos == null || fileInfos.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }

        validateFilesCount(fileInfos.size());
        validateFilesOrdering(fileInfos);

        for (FileUploadInfo fileInfo : fileInfos) {
            Files file = Files.create(this, fileInfo.getUrl(), fileInfo.getOrder());
            addFile(file);
        }
    }

    public void addFile(Files file) {
        this.files.add(file);
    }

    private void validateFilesCount(int newFilesCount) {
        int totalCount = this.files.size() + newFilesCount;
        if (totalCount > MAX_FILES_COUNT) {
            throw new CustomException(ErrorCode.TOO_MANY_FILES);
        }
    }

    private void validateFilesOrdering(List<FileUploadInfo> fileInfos) {
        Set<Integer> orders = new HashSet<>();

        for (FileUploadInfo fileInfo : fileInfos) {
            Integer order = fileInfo.getOrder();
            if (!orders.add(order)) {
                throw new CustomException(ErrorCode.DUPLICATE_FILE_ORDER);
            }
        }

        List<Integer> sortedOrders = orders.stream().sorted().toList();
        for (int i = 0; i < sortedOrders.size(); i++) {
            if (!sortedOrders.get(i).equals(i)) {
                throw new CustomException(ErrorCode.INVALID_FILE_ORDER);
            }
        }
    }

}
