package org.example.gridgestagram.repository.feed;

import org.example.gridgestagram.repository.feed.entity.Report;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, IReportRepository {

    boolean existsByTypeAndTargetIdAndReporterId(ReportType type, Long targetId, Long reporterId);

    Page<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
