package com.noerms.modules.registration.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Registration extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private Candidate candidate;
    @Column(name = "examination_session_id", nullable = false)
    private Long examinationSessionId;
    @Column(length = 50)
    private String status = "INCOMPLETE";
    @Column(name = "center_id")
    private Long centerId;
    @Column(name = "seat_number", length = 10)
    private String seatNumber;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
}
