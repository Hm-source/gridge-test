package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.entity.vo.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogSearchCondition {

    private LogType logType;
    private String category;
    private Long userId;
    private Role role;
    private String targetType;
    private Long targetId;
    private String description;
    private String ipAddress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String orderBy = "createdAt";
    private String direction = "DESC";
}