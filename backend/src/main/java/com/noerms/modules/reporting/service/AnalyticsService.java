package com.noerms.modules.reporting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * AnalyticsService — MODULE 6: Reporting & Analytics
 * National stats, subject performance, school rankings,
 * regional breakdown, gender analysis, malpractice analytics.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final JdbcTemplate jdbc;

    public Map<String, Object> getNationalOverview(Long sessionId) {
        log.debug("Computing national overview for session {}", sessionId);
        Map<String, Object> r = new LinkedHashMap<>();
        try {
            Long total = jdbc.queryForObject(
                "SELECT COUNT(DISTINCT candidate_id) FROM registrations WHERE examination_session_id=?",
                Long.class, sessionId);
            Double avg = jdbc.queryForObject(
                "SELECT COALESCE(AVG(raw_score),0) FROM scores WHERE examination_session_id=? AND moderation_status='APPROVED'",
                Double.class, sessionId);
            Long pass = jdbc.queryForObject(
                "SELECT COUNT(*) FROM results WHERE examination_session_id=? AND grade!='F' AND status='APPROVED'",
                Long.class, sessionId);
            Long total2 = jdbc.queryForObject(
                "SELECT COUNT(*) FROM results WHERE examination_session_id=? AND status='APPROVED'",
                Long.class, sessionId);
            double passRate = total2 != null && total2 > 0 ? (pass * 100.0) / total2 : 0;
            r.put("totalCandidates", total);
            r.put("averageScore",    Math.round((avg != null ? avg : 0) * 10.0) / 10.0);
            r.put("passRate",        Math.round(passRate * 10.0) / 10.0);
            r.put("failRate",        Math.round((100 - passRate) * 10.0) / 10.0);
        } catch (Exception e) {
            log.error("Error computing overview: {}", e.getMessage());
            r.put("error", e.getMessage());
        }
        return r;
    }

    public List<Map<String, Object>> getGradeDistribution(Long sessionId) {
        try {
            return jdbc.queryForList(
                "SELECT grade, COUNT(*) AS count, " +
                "ROUND(100.0*COUNT(*)/NULLIF(SUM(COUNT(*)) OVER(),0),1) AS percentage " +
                "FROM results WHERE examination_session_id=? AND status='APPROVED' " +
                "GROUP BY grade ORDER BY grade", sessionId);
        } catch (Exception e) {
            log.error("Error getting grades: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getSubjectPerformance(Long sessionId) {
        try {
            return jdbc.queryForList(
                "SELECT s.name AS subject_name, s.code, COUNT(sc.id) AS candidates, " +
                "ROUND(AVG(sc.raw_score)::NUMERIC,1) AS average_score, " +
                "MIN(sc.raw_score) AS min_score, MAX(sc.raw_score) AS max_score, " +
                "ROUND(100.0*COUNT(CASE WHEN sc.raw_score>=45 THEN 1 END)/NULLIF(COUNT(sc.id),0),1) AS pass_rate, " +
                "ROUND((100-AVG(sc.raw_score))::NUMERIC,1) AS difficulty_index " +
                "FROM subjects s JOIN scores sc ON s.id=sc.subject_id " +
                "WHERE sc.examination_session_id=? AND sc.moderation_status='APPROVED' " +
                "GROUP BY s.id, s.name, s.code ORDER BY average_score DESC", sessionId);
        } catch (Exception e) {
            log.error("Error getting subject performance: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getSchoolRankings(Long sessionId) {
        try {
            return jdbc.queryForList(
                "SELECT s.name AS school_name, s.region, COUNT(DISTINCT c.id) AS total_candidates, " +
                "ROUND(AVG(sc.raw_score)::NUMERIC,1) AS average_score, " +
                "ROUND(100.0*COUNT(CASE WHEN r.grade!='F' THEN 1 END)/NULLIF(COUNT(r.id),0),1) AS pass_rate, " +
                "COUNT(CASE WHEN r.grade='A' THEN 1 END) AS distinctions, " +
                "RANK() OVER (ORDER BY AVG(sc.raw_score) DESC) AS national_rank " +
                "FROM schools s JOIN candidates c ON s.id=c.school_id " +
                "JOIN scores sc ON c.id=sc.candidate_id AND sc.examination_session_id=? AND sc.moderation_status='APPROVED' " +
                "JOIN results r ON c.id=r.candidate_id AND r.examination_session_id=? " +
                "GROUP BY s.id, s.name, s.region ORDER BY national_rank LIMIT 100",
                sessionId, sessionId);
        } catch (Exception e) {
            log.error("Error getting school rankings: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getRegionalBreakdown(Long sessionId) {
        try {
            return jdbc.queryForList(
                "SELECT s.region, COUNT(DISTINCT c.id) AS total_candidates, " +
                "ROUND(AVG(sc.raw_score)::NUMERIC,1) AS average_score, " +
                "ROUND(100.0*COUNT(CASE WHEN r.grade!='F' THEN 1 END)/NULLIF(COUNT(r.id),0),1) AS pass_rate " +
                "FROM schools s JOIN candidates c ON s.id=c.school_id " +
                "JOIN scores sc ON c.id=sc.candidate_id AND sc.examination_session_id=? AND sc.moderation_status='APPROVED' " +
                "JOIN results r ON c.id=r.candidate_id AND r.examination_session_id=? " +
                "GROUP BY s.region ORDER BY pass_rate DESC",
                sessionId, sessionId);
        } catch (Exception e) {
            log.error("Error getting regional breakdown: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getGenderDistribution(Long sessionId) {
        try {
            return jdbc.queryForList(
                "SELECT c.gender, COUNT(DISTINCT c.id) AS total_candidates, " +
                "ROUND(AVG(sc.raw_score)::NUMERIC,1) AS average_score, " +
                "ROUND(100.0*COUNT(CASE WHEN r.grade!='F' THEN 1 END)/NULLIF(COUNT(r.id),0),1) AS pass_rate " +
                "FROM candidates c JOIN scores sc ON c.id=sc.candidate_id AND sc.examination_session_id=? " +
                "JOIN results r ON c.id=r.candidate_id AND r.examination_session_id=? " +
                "GROUP BY c.gender",
                sessionId, sessionId);
        } catch (Exception e) {
            log.error("Error getting gender distribution: {}", e.getMessage());
            return List.of();
        }
    }

    public Map<String, Object> getMalpracticeAnalytics() {
        Map<String, Object> data = new LinkedHashMap<>();
        try {
            data.put("total",    jdbc.queryForObject("SELECT COUNT(*) FROM malpractice_reports", Long.class));
            data.put("filed",    jdbc.queryForObject("SELECT COUNT(*) FROM malpractice_reports WHERE status='FILED'", Long.class));
            data.put("resolved", jdbc.queryForObject("SELECT COUNT(*) FROM malpractice_reports WHERE status='RESOLVED'", Long.class));
            data.put("byCenter", jdbc.queryForList(
                "SELECT ec.name AS center, ec.region, COUNT(mr.id) AS report_count " +
                "FROM examination_centers ec LEFT JOIN malpractice_reports mr ON ec.id=mr.center_id " +
                "GROUP BY ec.id, ec.name, ec.region ORDER BY report_count DESC LIMIT 10"));
        } catch (Exception e) {
            log.error("Error getting malpractice analytics: {}", e.getMessage());
            data.put("error", e.getMessage());
        }
        return data;
    }
}
