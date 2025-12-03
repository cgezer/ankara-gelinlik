package com.ankara_gelinlik.config;

import com.ankara_gelinlik.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final boolean IS_PROD = false; // LOCAL → false, PROD → true

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));  // Frontend URL
        configuration.setAllowedMethods(List.of("GET"));  // İlk başta sadece GET metodunu ekle

        // Ardından diğer metodları addAllowedMethod ile ekleyelim
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");

        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF'i devre dışı bırakıyoruz
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS yapılandırması
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/refresh", "/auth/me", "/auth/logout").permitAll()  // Auth endpoints herkese açık
                        .requestMatchers("/auth/medya/public/**").permitAll()  // Medya dosyaları herkese açık
                        .requestMatchers("/auth/yonetici/**").hasAuthority("ROLE_ADMIN")  // Yöneticiler için korumalı alan
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // OPTIONS istekleri herkese açık
                        .anyRequest().authenticated()  // Diğer tüm istekler kimlik doğrulaması gerektirir
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // JWT doğrulama filtresini ekliyoruz

        return http.build();
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }
}
