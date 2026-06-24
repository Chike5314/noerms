package com.noerms.modules.auth.service;

import com.noerms.config.JwtTokenProvider;
import com.noerms.modules.auth.dto.*;
import com.noerms.modules.auth.entity.User;
import com.noerms.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!user.getActive()) throw new RuntimeException("Account is deactivated");
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole(), user.getId());
        log.info("User logged in: {}", user.getEmail());
        return LoginResponse.builder()
                .success(true).token(token)
                .userId(user.getId()).email(user.getEmail())
                .fullName(user.getFullName()).role(user.getRole())
                .message("Login successful").build();
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already in use");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already in use");
        User user = User.builder()
                .email(request.getEmail()).username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName()).role("CANDIDATE")
                .phoneNumber(request.getPhoneNumber()).active(true).build();
        user = userRepository.save(user);
        return toDto(user);
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return UserDto.builder().id(user.getId()).email(user.getEmail())
                .username(user.getUsername()).fullName(user.getFullName())
                .role(user.getRole()).active(user.getActive()).build();
    }
}
