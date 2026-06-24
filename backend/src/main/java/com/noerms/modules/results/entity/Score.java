package com.noerms.modules.results.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Score extends BaseEntity {
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    @Column(name = "examination_session_id", nullable = false)
    private Long examinationSessionId;
    @Column(name = "raw_score", nullable = false)
    private Integer rawScore;
    @Column(name = "examiner_id", nullable = false)
    private Long examinerId;
    @Column(name = "marked_at")
    private LocalDateTime markedAt;
    @Column(name = "moderated_by_id")
    private Long moderatedById;
    @Column(name = "moderation_status", length = 50)
    private String moderationStatus = "PENDING";
    @Column(name = "moderation_notes")
    private String moderationNotes;
}
