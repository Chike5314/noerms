package com.noerms.modules.registration.entity;

import com.noerms.core.entity.BaseEntity;
import com.noerms.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Candidate — maps to Form G3 fields exactly as required by GCE Board
 */
@Entity
@Table(name = "candidates")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Candidate extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "candidate_number", unique = true, length = 20)
    private String candidateNumber;

    // ── Personal Information (Form G3) ──────────────────────
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth", length = 100)
    private String placeOfBirth;

    @Column(length = 10)
    private String gender;

    @Column(name = "national_id_number", length = 20)
    private String nationalIdNumber;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "parent_guardian_name", length = 200)
    private String parentGuardianName;

    @Column(name = "parent_guardian_phone", length = 20)
    private String parentGuardianPhone;

    @Column(name = "residential_address", length = 500)
    private String residentialAddress;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String division;

    // ── Academic Information ─────────────────────────────────
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Column(name = "school_name", length = 200)
    private String schoolName;

    @Column(name = "examination_type", nullable = false, length = 20)
    private String examinationType;  // O_LEVEL, A_LEVEL

    @Column(name = "series", length = 20)
    private String series;  // A, B, C, D, E (for A Level)

    // ── Registration Status ──────────────────────────────────
    @Column(name = "registration_status", length = 50)
    private String registrationStatus = "INCOMPLETE";

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    // ── Uploaded Document paths (stored as filenames/URLs) ───
    @Column(name = "birth_certificate_path", length = 500)
    private String birthCertificatePath;

    @Column(name = "passport_photo_path", length = 500)
    private String passportPhotoPath;

    @Column(name = "previous_results_path", length = 500)
    private String previousResultsPath;

    @Column(name = "school_letter_path", length = 500)
    private String schoolLetterPath;
}
