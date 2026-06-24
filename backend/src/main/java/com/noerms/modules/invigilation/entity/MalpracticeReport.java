package com.noerms.modules.invigilation.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "malpractice_reports")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MalpracticeReport extends BaseEntity {
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    @Column(name = "center_id", nullable = false)
    private Long centerId;
    @Column(name = "invigilator_id", nullable = false)
    private Long invigilatorId;
    @Column(nullable = false)
    private String description;
    @Column(length = 50)
    private String status = "FILED";
    @Column(name = "filed_at")
    private LocalDateTime filedAt = LocalDateTime.now();
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    @Column(name = "resolution_notes")
    private String resolutionNotes;
}
