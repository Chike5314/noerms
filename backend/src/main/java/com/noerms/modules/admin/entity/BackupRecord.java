package com.noerms.modules.admin.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_records")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BackupRecord extends BaseEntity {
    @Column(name = "backup_name", nullable = false, unique = true, length = 255)
    private String backupName;
    @Column(name = "backup_type", nullable = false, length = 50)
    private String backupType;
    @Column(name = "backup_path", length = 500)
    private String backupPath;
    @Column(name = "file_size_mb")
    private Integer fileSizeMb;
    @Column(length = 50)
    private String status = "PENDING";
    @Column(name = "initiated_by_id")
    private Long initiatedById;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
