package org.example.gridgestagram.repository.log;

import org.example.gridgestagram.repository.log.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long>,
    AdminLogRepositoryCustom {

}
