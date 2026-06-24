package com.noerms.modules.registration.repository;

import com.noerms.modules.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByCandidate_Id(Long candidateId);
    List<Registration> findByExaminationSessionId(Long sessionId);
    List<Registration> findByExaminationSessionIdAndStatus(Long sessionId, String status);
}
