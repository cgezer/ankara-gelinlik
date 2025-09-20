package com.ankara_gelinlik.config;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.YoneticiRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final YoneticiRepository yoneticiRepository;

    public SecurityConfig(YoneticiRepository yoneticiRepository) {
        this.yoneticiRepository = yoneticiRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // UserDetailsService veritabanı ile çalışacak şekilde
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Yonetici yonetici = yoneticiRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Yönetici bulunamadı: " + username));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(yonetici.getEmail())
                    .password(yonetici.getSifre())
                    .roles("ADMIN")
                    .build();
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll() // login ve static içerik serbest
                        .anyRequest().hasRole("ADMIN") // diğer tüm sayfalar sadece ADMIN
                )
                .formLogin(form -> form
                        .loginPage("/login")            // login sayfası
                        .defaultSuccessUrl("/yonetici") // başarılı giriş sonrası yönlendirme
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                )
                .csrf(csrf -> csrf.disable()); // CSRF kapalı (isteğe bağlı)

        return http.build();
    }
}
