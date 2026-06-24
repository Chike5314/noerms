package com.noerms.modules.admin.controller;

import com.noerms.modules.admin.service.AdminService;
import com.noerms.modules.auth.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(adminService.createUser(
                body.get("email"), body.get("username"), body.get("password"),
                body.get("fullName"), body.get("role")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok(Map.of("message", "User status updated"));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health() {
        return ResponseEntity.ok(adminService.getSystemHealth());
    }

    @PostMapping("/backup")
    public ResponseEntity<?> backup(Authentication auth) {
        // In real app, get userId from auth
        return ResponseEntity.ok(adminService.initiateBackup(1L));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<?> auditLogs(@RequestParam(defaultValue="50") int limit) {
        return ResponseEntity.ok(adminService.getAuditLogs(limit));
    }
}
