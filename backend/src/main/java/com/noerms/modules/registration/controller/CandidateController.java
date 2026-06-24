package com.noerms.modules.registration.controller;

import com.noerms.modules.registration.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    /** GET /api/candidate/profile — full profile + registration status */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            return ResponseEntity.ok(candidateService.getCandidateProfile(auth.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/candidate/form-g3 — submit personal info */
    @PostMapping("/form-g3")
    public ResponseEntity<?> submitFormG3(@RequestBody Map<String, Object> formData,
                                          Authentication auth) {
        try {
            return ResponseEntity.ok(candidateService.submitFormG3(auth.getName(), formData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/candidate/subjects — select subjects */
    @PostMapping("/subjects")
    public ResponseEntity<?> selectSubjects(@RequestBody Map<String, Object> body,
                                            Authentication auth) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> raw = (List<Integer>) body.get("subjectIds");
            List<Long> ids = raw.stream().map(Long::valueOf).toList();
            return ResponseEntity.ok(candidateService.selectSubjects(auth.getName(), ids));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/candidate/payment — record payment */
    @PostMapping("/payment")
    public ResponseEntity<?> recordPayment(@RequestBody Map<String, Object> payData,
                                           Authentication auth) {
        try {
            return ResponseEntity.ok(candidateService.recordPayment(auth.getName(), payData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/candidate/documents/{type} — confirm document upload */
    @PostMapping("/documents/{documentType}")
    public ResponseEntity<?> uploadDocument(@PathVariable String documentType,
                                            Authentication auth) {
        try {
            return ResponseEntity.ok(
                candidateService.confirmDocumentUpload(auth.getName(), documentType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/candidate/results — get own results */
    @GetMapping("/results")
    public ResponseEntity<?> getResults(Authentication auth) {
        try {
            return ResponseEntity.ok(candidateService.getCandidateResults(auth.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
