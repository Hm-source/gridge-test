package org.example.gridgestagram.repository.feed;

import java.time.LocalDate;
import org.example.gridgestagram.controller.admin.dto.ReportQueryDto;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepositoryCustom {

    Page<ReportQueryDto> findReports(Pageable pageable,
        ReportStatus status,
        String reporterName,
        String writerName,
        LocalDate startDate,
        LocalDate endDate
    );
}
