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
    public CommandLineRunner createDefaultAdmin(YoneticiRepository repo, PasswordEncoder encoder) {
        return args -> {
            String email = "admin@example.com";
            if (repo.findByEmail(email).isEmpty()) {
                Yonetici admin = new Yonetici();
                admin.setAd("Admin");
                admin.setSoyad("User");
                admin.setEmail(email);
                admin.setSifre(encoder.encode("123")); // hashlenmiş şifre
                admin.setRole("ROLE_ADMIN");
                repo.save(admin);
                System.out.println("Default admin created: " + email + " / 123");
            } else {
                System.out.println("Admin already exists: " + email);
            }
        };
    }


}
