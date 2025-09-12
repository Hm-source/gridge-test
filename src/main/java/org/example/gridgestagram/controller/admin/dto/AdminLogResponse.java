package org.example.gridgestagram.controller.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.log.entity.AdminLog;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.entity.vo.Role;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogResponse {

    private Long id;
    private LogType logType;
    private String logTypeDescription;
    private String category;
    private Long userId;
    private String username;
    private String name;
    private Role role;
    private String targetType;
    private Long targetId;
    private String description;
    private String metadata;
    private LocalDateTime createdAt;

    public static AdminLogResponse from(AdminLog log) {
        return AdminLogResponse.builder()
            .id(log.getId())
            .logType(log.getLogType())
            .logTypeDescription(log.getLogType().getDescription())
            .category(log.getLogType().getCategory())
            .userId(log.getUserId())
            .role(log.getRole())
            .targetType(log.getTargetType())
            .targetId(log.getTargetId())
            .description(log.getDescription())
            .metadata(log.getMetadata())
            .createdAt(log.getCreatedAt())
            .build();
    }

    public static AdminLogResponse fromWithUserInfo(AdminLog log, String username,
        String name, Role role) {
        AdminLogResponse response = from(log);
        response.username = username;
        response.name = name;
        response.role = role;
        return response;
    }
}

