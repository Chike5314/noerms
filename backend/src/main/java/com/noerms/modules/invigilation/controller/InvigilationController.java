package com.noerms.modules.invigilation.controller;

import com.noerms.modules.invigilation.entity.*;
import com.noerms.modules.invigilation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class InvigilationController {
    private final AttendanceRepository attendanceRepository;
    private final MalpracticeRepository malpracticeRepository;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Map<String, Object> body) {
        try {
            AttendanceRecord record = AttendanceRecord.builder()
                .candidateId(Long.parseLong(body.get("candidateId").toString()))
                .examinationSessionId(Long.parseLong(body.get("examinationSessionId").toString()))
                .centerId(Long.parseLong(body.get("centerId").toString()))
                .subjectId(Long.parseLong(body.get("subjectId").toString()))
                .attendanceStatus(body.get("status").toString())
                .markedById(Long.parseLong(body.get("markedById").toString())).build();
            return ResponseEntity.ok(attendanceRepository.save(record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/center/{centerId}/session/{sessionId}")
    public ResponseEntity<List<AttendanceRecord>> getCenterAttendance(@PathVariable Long centerId, @PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceRepository.findByCenterIdAndExaminationSessionId(centerId, sessionId));
    }

    @PostMapping("/malpractice")
    public ResponseEntity<?> reportMalpractice(@RequestBody Map<String, Object> body) {
        MalpracticeReport report = MalpracticeReport.builder()
            .candidateId(Long.parseLong(body.get("candidateId").toString()))
            .centerId(Long.parseLong(body.get("centerId").toString()))
            .invigilatorId(Long.parseLong(body.get("invigilatorId").toString()))
            .description(body.get("description").toString()).status("FILED").build();
        return ResponseEntity.ok(malpracticeRepository.save(report));
    }

    @GetMapping("/malpractice")
    public ResponseEntity<List<MalpracticeReport>> getMalpracticeReports() {
        return ResponseEntity.ok(malpracticeRepository.findByStatus("FILED"));
    }
}
