package com.noerms.modules.auth.entity;

import com.noerms.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(nullable = false, length = 50)
    private String role;
    @Column(name = "full_name", length = 200)
    private String fullName;
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    @Column(nullable = false)
    private Boolean active = true;
    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled = false;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}
