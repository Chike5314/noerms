package com.noerms.modules.invigilation.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceRecord extends BaseEntity {
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    @Column(name = "examination_session_id", nullable = false)
    private Long examinationSessionId;
    @Column(name = "center_id", nullable = false)
    private Long centerId;
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    @Column(name = "attendance_status", nullable = false, length = 20)
    private String attendanceStatus;
    @Column(name = "marked_by_id", nullable = false)
    private Long markedById;
    @Column(name = "marked_at")
    private LocalDateTime markedAt = LocalDateTime.now();
}
