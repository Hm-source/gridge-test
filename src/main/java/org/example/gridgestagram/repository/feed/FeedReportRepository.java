package org.example.gridgestagram.repository.feed;

import org.example.gridgestagram.repository.feed.entity.FeedReport;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

    boolean existsByFeedIdAndReporterId(Long feedId, Long reporterId);

    Page<FeedReport> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    Page<FeedReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

