package com.noerms.modules.results.repository;

import com.noerms.modules.results.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByCandidateIdAndExaminationSessionId(Long candidateId, Long sessionId);
    List<Score> findByExaminationSessionIdAndModerationStatus(Long sessionId, String status);
    Optional<Score> findByCandidateIdAndSubjectIdAndExaminationSessionId(Long c, Long s, Long e);
    @Query("SELECT AVG(s.rawScore) FROM Score s WHERE s.examinationSessionId = :sessionId AND s.subjectId = :subjectId")
    Double findAverageBySessionAndSubject(Long sessionId, Long subjectId);
}
