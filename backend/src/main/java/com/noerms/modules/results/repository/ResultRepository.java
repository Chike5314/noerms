package com.noerms.modules.results.repository;

import com.noerms.modules.results.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByCandidateId(Long candidateId);
    List<Result> findByCandidateIdAndExaminationSessionId(Long candidateId, Long sessionId);
    List<Result> findByExaminationSessionIdAndStatus(Long sessionId, String status);
    long countByExaminationSessionIdAndGrade(Long sessionId, String grade);
}
