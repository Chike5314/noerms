package com.noerms.modules.registration.repository;

import com.noerms.modules.registration.entity.Candidate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByUser_Id(Long userId);
    Optional<Candidate> findByCandidateNumber(String candidateNumber);
    List<Candidate> findBySchoolId(Long schoolId);
    List<Candidate> findByRegistrationStatus(String status);
    Page<Candidate> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName, Pageable pageable);
    @Query("SELECT c FROM Candidate c WHERE c.schoolId = :schoolId AND c.registrationStatus = :status")
    List<Candidate> findBySchoolIdAndStatus(Long schoolId, String status);
}
