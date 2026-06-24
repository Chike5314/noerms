package com.noerms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Public — no token needed ──────────────────
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/health",
                    "/api/health",
                    "/actuator/health"
                ).permitAll()

                // ── Role-based endpoints ───────────────────────
                .requestMatchers("/api/admin/**")
                    .hasAnyRole("SYSTEM_ADMIN", "NATIONAL_ADMIN")
                .requestMatchers("/api/results/scores/**")
                    .hasAnyRole("EXAMINER", "NATIONAL_ADMIN", "SYSTEM_ADMIN")
                .requestMatchers("/api/results/approve/**")
                    .hasAnyRole("NATIONAL_ADMIN", "MINISTRY_OFFICIAL")
                .requestMatchers("/api/attendance/**")
                    .hasAnyRole("INVIGILATOR", "NATIONAL_ADMIN", "SYSTEM_ADMIN")
                .requestMatchers("/api/schedule/**")
                    .hasAnyRole("NATIONAL_ADMIN", "SYSTEM_ADMIN")
                .requestMatchers("/api/analytics/**")
                    .hasAnyRole("NATIONAL_ADMIN", "MINISTRY_OFFICIAL", "SYSTEM_ADMIN")

                // ── Candidate self-service ───────────────────
                .requestMatchers("/api/candidate/**").authenticated()

                // ── Everything else needs a valid token ────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow ALL origins — works on localhost, LAN IPs, deployed servers
        // For production, replace with your specific domain
        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
