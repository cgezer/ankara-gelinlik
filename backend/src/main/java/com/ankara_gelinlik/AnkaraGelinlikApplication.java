package com.ankara_gelinlik;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.enums.Role;
import com.ankara_gelinlik.repository.YoneticiRepository;
import com.ankara_gelinlik.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AnkaraGelinlikApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnkaraGelinlikApplication.class, args);
    }

    @Bean
    public CommandLineRunner createDefaultAdmin(YoneticiRepository repo, PasswordEncoder encoder, RefreshTokenService refreshTokenService) {
        return args -> {
            String email = "admin1@example.com";

            // --- Admin oluştur ---
            if (repo.findByEmail(email).isEmpty()) {
                Yonetici admin = new Yonetici();
                admin.setAd("Admin");
                admin.setSoyad("User");
                admin.setEmail(email);
                admin.setSifre(encoder.encode("123"));
                admin.setRole(Role.ROLE_ADMIN); // Enum uyumlu
                repo.save(admin);
                System.out.println("Default admin created: " + email + " / 123");
            } else {
                System.out.println("Admin already exists: " + email);
            }

            // --- RefreshTokenService testi ---
            System.out.println("Creating refresh token for: " + email);
            var token = refreshTokenService.createOrReuseRefreshToken(email);
            System.out.println("Refresh token created: " + token.getToken() + " | expiry: " + token.getExpiryDate());
        };
    }
}
