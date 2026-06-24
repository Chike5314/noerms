package com.noerms.modules.registration.repository;

import com.noerms.modules.registration.entity.SubjectSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectSelectionRepository extends JpaRepository<SubjectSelection, Long> {
    List<SubjectSelection> findByCandidateIdAndExaminationSessionId(Long candidateId, Long sessionId);
    long countByCandidateIdAndExaminationSessionId(Long candidateId, Long sessionId);
}
