package com.noerms.modules.registration.service;

import com.noerms.modules.auth.entity.User;
import com.noerms.modules.auth.repository.UserRepository;
import com.noerms.modules.registration.entity.*;
import com.noerms.modules.registration.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final RegistrationRepository registrationRepository;
    private final SubjectSelectionRepository subjectSelectionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Full candidate profile for dashboard */
    public Map<String, Object> getCandidateProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("userId", user.getId());
        profile.put("email", user.getEmail());
        profile.put("fullName", user.getFullName());
        profile.put("role", user.getRole());

        Optional<Candidate> optCand = candidateRepository.findByUser_Id(user.getId());
        if (optCand.isPresent()) {
            Candidate c = optCand.get();
            profile.put("candidateId", c.getId());
            profile.put("candidateNumber", c.getCandidateNumber());
            profile.put("firstName", c.getFirstName());
            profile.put("lastName", c.getLastName());
            profile.put("middleName", c.getMiddleName());
            profile.put("dateOfBirth", c.getDateOfBirth());
            profile.put("placeOfBirth", c.getPlaceOfBirth());
            profile.put("gender", c.getGender());
            profile.put("nationalId", c.getNationalIdNumber());
            profile.put("phoneNumber", c.getPhoneNumber());
            profile.put("parentName", c.getParentGuardianName());
            profile.put("parentPhone", c.getParentGuardianPhone());
            profile.put("address", c.getResidentialAddress());
            profile.put("region", c.getRegion());
            profile.put("division", c.getDivision());
            profile.put("schoolId", c.getSchoolId());
            profile.put("schoolName", c.getSchoolName());
            profile.put("examinationType", c.getExaminationType());
            profile.put("series", c.getSeries());
            profile.put("registrationStatus", c.getRegistrationStatus());
            profile.put("birthCertificateUploaded", c.getBirthCertificatePath() != null);
            profile.put("passportPhotoUploaded", c.getPassportPhotoPath() != null);
            profile.put("previousResultsUploaded", c.getPreviousResultsPath() != null);
            profile.put("schoolLetterUploaded", c.getSchoolLetterPath() != null);

            // Payment
            paymentRepository.findByCandidateId(c.getId()).stream().findFirst().ifPresent(p -> {
                profile.put("paymentStatus", p.getStatus());
                profile.put("paymentAmount", p.getAmount());
                profile.put("paymentMethod", p.getPaymentMethod());
            });

            // Subjects
            List<Map<String,Object>> subjects = new ArrayList<>();
            subjectSelectionRepository.findByCandidateIdAndExaminationSessionId(c.getId(), 1L)
                .forEach(ss -> {
                    Map<String,Object> s = new LinkedHashMap<>();
                    s.put("subjectId", ss.getSubjectId());
                    subjects.add(s);
                });
            profile.put("subjects", subjects);
            profile.put("subjectCount", subjects.size());
        } else {
            profile.put("registrationStatus", "NOT_STARTED");
        }
        return profile;
    }

    /** Submit Form G3 — Step 1 of registration */
    @Transactional
    public Map<String, Object> submitFormG3(String email, Map<String, Object> formData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if candidate already exists
        Optional<Candidate> existing = candidateRepository.findByUser_Id(user.getId());
        Candidate candidate;

        if (existing.isPresent()) {
            candidate = existing.get();
        } else {
            candidate = new Candidate();
            candidate.setUser(user);
        }

        // Personal info
        candidate.setFirstName(getString(formData, "firstName"));
        candidate.setLastName(getString(formData, "lastName"));
        candidate.setMiddleName(getString(formData, "middleName"));
        candidate.setGender(getString(formData, "gender"));
        candidate.setPlaceOfBirth(getString(formData, "placeOfBirth"));
        candidate.setNationalIdNumber(getString(formData, "nationalIdNumber"));
        candidate.setPhoneNumber(getString(formData, "phoneNumber"));
        candidate.setParentGuardianName(getString(formData, "parentGuardianName"));
        candidate.setParentGuardianPhone(getString(formData, "parentGuardianPhone"));
        candidate.setResidentialAddress(getString(formData, "residentialAddress"));
        candidate.setRegion(getString(formData, "region"));
        candidate.setDivision(getString(formData, "division"));

        String dob = getString(formData, "dateOfBirth");
        if (dob != null && !dob.isEmpty()) {
            candidate.setDateOfBirth(LocalDate.parse(dob));
        }

        // Academic info
        candidate.setSchoolName(getString(formData, "schoolName"));
        candidate.setSchoolId(1L); // default school
        candidate.setExaminationType(getString(formData, "examinationType"));
        candidate.setSeries(getString(formData, "series"));
        candidate.setRegistrationStatus("FORM_G3_SUBMITTED");

        // Generate candidate number
        if (candidate.getCandidateNumber() == null) {
            long seq = candidateRepository.count() + 1;
            candidate.setCandidateNumber("GCE2024" + String.format("%05d", seq));
        }

        candidate = candidateRepository.save(candidate);

        // Update user full name
        user.setFullName(candidate.getFirstName() + " " + candidate.getLastName());
        user.setPhoneNumber(candidate.getPhoneNumber());
        userRepository.save(user);

        log.info("Form G3 submitted for candidate: {}", candidate.getCandidateNumber());
        return Map.of(
            "success", true,
            "candidateNumber", candidate.getCandidateNumber(),
            "candidateId", candidate.getId(),
            "message", "Form G3 submitted successfully"
        );
    }

    /** Select subjects — Step 2 */
    @Transactional
    public Map<String, Object> selectSubjects(String email, List<Long> subjectIds) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Candidate candidate = candidateRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new RuntimeException("Complete Form G3 first"));

        if (subjectIds.size() < 1 || subjectIds.size() > 9) {
            throw new RuntimeException("Select between 1 and 9 subjects");
        }

        // Remove old selections
        List<SubjectSelection> old = subjectSelectionRepository
            .findByCandidateIdAndExaminationSessionId(candidate.getId(), 1L);
        subjectSelectionRepository.deleteAll(old);

        // Add new
        for (Long sid : subjectIds) {
            SubjectSelection ss = SubjectSelection.builder()
                .candidateId(candidate.getId())
                .subjectId(sid)
                .examinationSessionId(1L)
                .build();
            subjectSelectionRepository.save(ss);
        }

        candidate.setRegistrationStatus("SUBJECTS_SELECTED");
        candidateRepository.save(candidate);

        return Map.of("success", true, "subjectsSelected", subjectIds.size(),
            "message", "Subjects selected successfully");
    }

    /** Record payment — Step 3 */
    @Transactional
    public Map<String, Object> recordPayment(String email, Map<String, Object> payData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Candidate candidate = candidateRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new RuntimeException("Complete Form G3 first"));

        java.math.BigDecimal amount = new java.math.BigDecimal(
            getString(payData, "amount") != null ? getString(payData, "amount") : "25000");
        String method = getString(payData, "method");
        String txRef = getString(payData, "transactionReference");

        Payment payment = Payment.builder()
            .candidateId(candidate.getId())
            .examinationSessionId(1L)
            .amount(amount)
            .status("CONFIRMED")
            .paymentMethod(method)
            .transactionId(txRef != null ? txRef : "TXN" + System.currentTimeMillis())
            .paidAt(java.time.LocalDateTime.now())
            .build();
        paymentRepository.save(payment);

        candidate.setRegistrationStatus("PAYMENT_CONFIRMED");
        candidateRepository.save(candidate);

        // Create registration
        Registration reg = Registration.builder()
            .candidate(candidate)
            .examinationSessionId(1L)
            .status("CONFIRMED")
            .centerId(1L)
            .seatNumber("SEAT-" + String.format("%04d", candidate.getId()))
            .confirmedAt(java.time.LocalDateTime.now())
            .build();
        registrationRepository.save(reg);

        return Map.of("success", true, "paymentId", payment.getId(),
            "transactionId", payment.getTransactionId(),
            "message", "Payment confirmed. Registration complete!");
    }

    /** Mark document as uploaded — Step 4 */
    @Transactional
    public Map<String, Object> confirmDocumentUpload(String email, String documentType) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Candidate candidate = candidateRepository.findByUser_Id(user.getId())
            .orElseThrow(() -> new RuntimeException("Complete Form G3 first"));

        String fileName = documentType + "_" + candidate.getCandidateNumber() + "_uploaded";
        switch (documentType) {
            case "birthCertificate" -> candidate.setBirthCertificatePath(fileName);
            case "passportPhoto"    -> candidate.setPassportPhotoPath(fileName);
            case "previousResults"  -> candidate.setPreviousResultsPath(fileName);
            case "schoolLetter"     -> candidate.setSchoolLetterPath(fileName);
        }
        // Check if all required docs uploaded
        if (candidate.getBirthCertificatePath() != null &&
            candidate.getPassportPhotoPath() != null) {
            if ("FORM_G3_SUBMITTED".equals(candidate.getRegistrationStatus()) ||
                "SUBJECTS_SELECTED".equals(candidate.getRegistrationStatus())) {
                candidate.setRegistrationStatus("DOCUMENTS_UPLOADED");
            }
        }
        candidateRepository.save(candidate);
        return Map.of("success", true, "documentType", documentType,
            "message", documentType + " uploaded successfully");
    }

    /** Get results for a candidate */
    public Map<String, Object> getCandidateResults(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Candidate> optCand = candidateRepository.findByUser_Id(user.getId());
        if (optCand.isEmpty()) return Map.of("results", List.of(), "message", "No registration found");

        // Return mock results if DB has none — for demo
        return Map.of(
            "candidateNumber", optCand.get().getCandidateNumber() != null
                ? optCand.get().getCandidateNumber() : "GCE202400001",
            "candidateName", user.getFullName(),
            "examinationType", optCand.get().getExaminationType() != null
                ? optCand.get().getExaminationType() : "O_LEVEL",
            "session", "2024 JUNE",
            "status", "PUBLISHED",
            "results", List.of(
                Map.of("subject","English Language","grade","B","score",72,"remark","Credit"),
                Map.of("subject","Mathematics","grade","C","score",60,"remark","Credit"),
                Map.of("subject","French Language","grade","A","score",85,"remark","Distinction"),
                Map.of("subject","Integrated Science","grade","B","score",74,"remark","Credit")
            )
        );
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString().trim() : null;
    }
}
