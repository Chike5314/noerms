package com.noerms.modules.invigilation.repository;

import com.noerms.modules.invigilation.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByCenterIdAndExaminationSessionId(Long centerId, Long sessionId);
    Optional<AttendanceRecord> findByCandidateIdAndSubjectIdAndExaminationSessionId(Long c, Long s, Long e);
    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.centerId=:centerId AND a.examinationSessionId=:sessionId AND a.attendanceStatus='PRESENT'")
    long countPresentByCenter(Long centerId, Long sessionId);
}
