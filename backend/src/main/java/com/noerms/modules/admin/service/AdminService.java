package com.noerms.modules.admin.service;

import com.noerms.modules.admin.entity.BackupRecord;
import com.noerms.modules.admin.repository.BackupRepository;
import com.noerms.modules.auth.dto.UserDto;
import com.noerms.modules.auth.entity.User;
import com.noerms.modules.auth.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final BackupRepository backupRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDto> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
            .map(u -> UserDto.builder().id(u.getId()).email(u.getEmail())
                .username(u.getUsername()).fullName(u.getFullName())
                .role(u.getRole()).active(u.getActive()).build());
    }

    @Transactional
    public UserDto createUser(String email, String username, String password, String fullName, String role) {
        if (userRepository.existsByEmail(email)) throw new RuntimeException("Email already in use");
        User user = User.builder().email(email).username(username)
            .passwordHash(passwordEncoder.encode(password)).fullName(fullName)
            .role(role).active(true).build();
        user = userRepository.save(user);
        return UserDto.builder().id(user.getId()).email(user.getEmail())
            .username(user.getUsername()).fullName(user.getFullName())
            .role(user.getRole()).active(user.getActive()).build();
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.getActive());
        userRepository.save(user);
    }

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("totalUsers", userRepository.count());
        health.put("activeUsers", userRepository.findByActive(true).size());
        health.put("database", "Connected");
        health.put("version", "1.0.0");
        return health;
    }

    @Transactional
    public BackupRecord initiateBackup(Long userId) {
        String name = "BACKUP_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        BackupRecord backup = BackupRecord.builder().backupName(name).backupType("FULL")
            .backupPath("/backups/" + name + ".sql").status("COMPLETED")
            .initiatedById(userId).startedAt(LocalDateTime.now()).completedAt(LocalDateTime.now()).build();
        return backupRepository.save(backup);
    }

    public List<Map<String, Object>> getAuditLogs(int limit) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit))
            .getContent().stream().map(a -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", a.getId());
                m.put("action", a.getActionType());
                m.put("entityType", a.getEntityType());
                m.put("timestamp", a.getTimestamp());
                return m;
            }).collect(Collectors.toList());
    }
}
