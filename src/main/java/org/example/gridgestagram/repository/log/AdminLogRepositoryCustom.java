package org.example.gridgestagram.repository.log;

import org.example.gridgestagram.controller.admin.dto.AdminLogSearchCondition;
import org.example.gridgestagram.repository.log.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminLogRepositoryCustom {

    public Page<AdminLog> searchLogs(AdminLogSearchCondition condition, Pageable pageable);
}
