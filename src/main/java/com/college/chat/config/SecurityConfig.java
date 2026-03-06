package com.college.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable for simplicity in college project
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/admin.html", "/css/**", "/js/**", "/ws-chat/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}