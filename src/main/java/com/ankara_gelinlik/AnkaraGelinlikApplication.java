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
    CommandLineRunner init(YoneticiRepository yoneticiRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (yoneticiRepository.count() == 0) {
                // Test yönetici 1
                Yonetici y1 = new Yonetici();
                y1.setAd("Admin");
                y1.setSoyad("User");
                y1.setEmail("admin@example.com");
                y1.setSifre(passwordEncoder.encode("12345"));
                yoneticiRepository.save(y1);

                // Test yönetici 2
                Yonetici y2 = new Yonetici();
                y2.setAd("Test");
                y2.setSoyad("User");
                y2.setEmail("test@example.com");
                y2.setSifre(passwordEncoder.encode("password"));
                yoneticiRepository.save(y2);

                System.out.println("Test yöneticiler eklendi!");
            }
        };
    }
}
