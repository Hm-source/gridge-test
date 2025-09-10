package org.example.gridgestagram.repository.feed;

import org.example.gridgestagram.controller.admin.dto.ReportQueryDto;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReportRepository {

    Page<ReportQueryDto> findReports(Pageable pageable, ReportStatus status);
}
