package com.noerms.modules.registration.repository;

import com.noerms.modules.registration.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCandidateId(Long candidateId);
    Optional<Payment> findByCandidateIdAndExaminationSessionId(Long candidateId, Long sessionId);
    List<Payment> findByStatus(String status);
}
