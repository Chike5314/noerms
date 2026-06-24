package com.noerms.modules.reporting.controller;

import com.noerms.modules.reporting.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<?> overview(@RequestParam(defaultValue="1") Long sessionId) {
        try { return ResponseEntity.ok(analyticsService.getNationalOverview(sessionId)); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/grades")
    public ResponseEntity<?> grades(@RequestParam(defaultValue="1") Long sessionId) {
        try { return ResponseEntity.ok(analyticsService.getGradeDistribution(sessionId)); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/subjects")
    public ResponseEntity<?> subjects(@RequestParam(defaultValue="1") Long sessionId) {
        try { return ResponseEntity.ok(analyticsService.getSubjectPerformance(sessionId)); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/schools")
    public ResponseEntity<?> schools(@RequestParam(defaultValue="1") Long sessionId) {
        try { return ResponseEntity.ok(analyticsService.getSchoolRankings(sessionId)); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/regional")
    public ResponseEntity<?> regional(@RequestParam(defaultValue="1") Long sessionId) {
        try { return ResponseEntity.ok(analyticsService.getRegionalBreakdown(sessionId)); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/gender")
    public ResponseEntity<?> gender(@RequestParam(defaultValue="1") Long sessionId) {
        try { return ResponseEntity.ok(analyticsService.getGenderDistribution(sessionId)); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/malpractice")
    public ResponseEntity<?> malpractice() {
        try { return ResponseEntity.ok(analyticsService.getMalpracticeAnalytics()); }
        catch (Exception e) { return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage())); }
    }
}
