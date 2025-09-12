package org.example.gridgestagram.repository.log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.repository.user.entity.vo.Role;

@Entity
@Table(name = "admin_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType logType;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "JSON")
    private String metadata;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static AdminLog create(LogType logType, Long userId, Role role, String description) {
        return AdminLog.builder()
            .logType(logType)
            .userId(userId)
            .role(role)
            .description(description)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static AdminLog createWithTarget(LogType logType, Long userId, Role role,
        String targetType,
        Long targetId, String description) {
        return AdminLog.builder()
            .logType(logType)
            .userId(userId)
            .role(role)
            .targetType(targetType)
            .targetId(targetId)
            .description(description)
            .createdAt(LocalDateTime.now())
            .build();
    }
}