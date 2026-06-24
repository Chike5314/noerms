package com.noerms.modules.auth.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;
    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;
    @Column(name = "entity_type", length = 50)
    private String entityType;
    @Column(name = "entity_id")
    private Long entityId;
    @Column(name = "change_description")
    private String changeDescription;
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
