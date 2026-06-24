package com.noerms.modules.registration.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subject_selections")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SubjectSelection extends BaseEntity {
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    @Column(name = "examination_session_id", nullable = false)
    private Long examinationSessionId;
    @Column(name = "selected_at")
    private LocalDateTime selectedAt = LocalDateTime.now();
}
