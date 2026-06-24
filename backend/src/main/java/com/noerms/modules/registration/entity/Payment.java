package com.noerms.modules.registration.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    @Column(name = "examination_session_id", nullable = false)
    private Long examinationSessionId;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(length = 50)
    private String status = "PENDING";
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
