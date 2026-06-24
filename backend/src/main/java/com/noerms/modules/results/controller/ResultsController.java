package com.noerms.modules.results.controller;

import com.noerms.modules.results.entity.*;
import com.noerms.modules.results.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultsController {
    private final ScoreRepository scoreRepository;
    private final ResultRepository resultRepository;

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Result>> getCandidateResults(@PathVariable Long candidateId) {
        return ResponseEntity.ok(resultRepository.findByCandidateId(candidateId));
    }

    @GetMapping("/scores/candidate/{candidateId}/session/{sessionId}")
    public ResponseEntity<List<Score>> getCandidateScores(@PathVariable Long candidateId, @PathVariable Long sessionId) {
        return ResponseEntity.ok(scoreRepository.findByCandidateIdAndExaminationSessionId(candidateId, sessionId));
    }

    @PostMapping("/scores")
    public ResponseEntity<?> submitScore(@RequestBody Map<String, Object> body) {
        try {
            Score score = Score.builder()
                .candidateId(Long.parseLong(body.get("candidateId").toString()))
                .subjectId(Long.parseLong(body.get("subjectId").toString()))
                .examinationSessionId(Long.parseLong(body.get("examinationSessionId").toString()))
                .rawScore(Integer.parseInt(body.get("rawScore").toString()))
                .examinerId(Long.parseLong(body.get("examinerId").toString()))
                .moderationStatus("PENDING").markedAt(LocalDateTime.now()).build();
            return ResponseEntity.ok(scoreRepository.save(score));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionId}/stats")
    public ResponseEntity<Map<String, Object>> getSessionStats(@PathVariable Long sessionId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("sessionId", sessionId);
        stats.put("totalScores", scoreRepository.findByExaminationSessionIdAndModerationStatus(sessionId, "APPROVED").size());
        stats.put("published", resultRepository.findByExaminationSessionIdAndStatus(sessionId, "PUBLISHED").size());
        return ResponseEntity.ok(stats);
    }
}
