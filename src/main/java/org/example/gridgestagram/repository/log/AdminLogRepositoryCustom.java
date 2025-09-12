package org.example.gridgestagram.repository.log;

import java.util.List;
import org.example.gridgestagram.controller.admin.dto.AdminLogSearchCondition;
import org.example.gridgestagram.repository.log.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminLogRepositoryCustom {

    public List<AdminLog> findRecentLogs(int limit);

    public List<AdminLog> findLogsByUser(Long userId, int days, int limit);

    public Page<AdminLog> searchLogs(AdminLogSearchCondition condition, Pageable pageable);
}
