package com.noerms.modules.results.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Result extends BaseEntity {
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    @Column(name = "examination_session_id", nullable = false)
    private Long examinationSessionId;
    @Column(nullable = false, length = 2)
    private String grade;
    @Column(length = 50)
    private String status = "PENDING_APPROVAL";
    @Column(name = "computed_at")
    private LocalDateTime computedAt;
    @Column(name = "ministry_approved_at")
    private LocalDateTime ministryApprovedAt;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
