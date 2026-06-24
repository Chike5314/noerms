package com.noerms.modules.invigilation.repository;

import com.noerms.modules.invigilation.entity.MalpracticeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MalpracticeRepository extends JpaRepository<MalpracticeReport, Long> {
    List<MalpracticeReport> findByCenterId(Long centerId);
    List<MalpracticeReport> findByStatus(String status);
    List<MalpracticeReport> findByCandidateId(Long candidateId);
}
