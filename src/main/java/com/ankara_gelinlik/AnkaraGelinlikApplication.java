package com.ankara_gelinlik;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.YoneticiRepository;
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
    public CommandLineRunner init(YoneticiRepository yoneticiRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@example.com";
            if (yoneticiRepository.findByEmail(adminEmail).isEmpty()) {
                Yonetici admin = new Yonetici();
                admin.setAd("Admin");
                admin.setSoyad("User");
                admin.setEmail(adminEmail);
                admin.setRole("ADMIN"); // DB'ye "ADMIN" kaydediyoruz (SecurityConfig bunu ROLE_ADMIN olarak kullanacak)
                admin.setSifre(passwordEncoder.encode("123456"));
                yoneticiRepository.save(admin);
                System.out.println("🔧 Admin created: " + adminEmail);
            } else {
                System.out.println("ℹ️ Admin already exists: " + adminEmail);
            }
        };
    }
}
