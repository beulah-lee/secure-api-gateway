package com.example.secureapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.secureapi.security.JwtFilter;
import com.example.secureapi.security.RateLimitingFilter;
import com.example.secureapi.util.JwtUtil;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RateLimitingFilter rateLimitingFilter;
    
    @Value("${security.enable-rate-limiting:true}")
    private boolean enableRateLimiting;

    public SecurityConfig(JwtUtil jwtUtil, RateLimitingFilter rateLimitingFilter) {
        this.jwtUtil = jwtUtil;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login").permitAll()
                .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN", "READ_ONLY")
                .anyRequest().authenticated()
            );
            
        if (enableRateLimiting) {
            http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        }
        http.addFilter(new JwtFilter(authenticationManager, jwtUtil));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}