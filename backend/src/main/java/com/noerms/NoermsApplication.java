package com.noerms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class NoermsApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoermsApplication.class, args);
        System.out.println("""
            ╔══════════════════════════════════════════════════════════════╗
            ║   🍏 NOERMS - National Examination Management System         ║
            ║   University of Buea | CEF476 | Version 1.0.0               ║
            ║   Backend: http://localhost:8080/api                         ║
            ║   Frontend: http://localhost:8000                            ║
            ╚══════════════════════════════════════════════════════════════╝
        """);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
