package com.ankara_gelinlik;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.enums.Role;
import com.ankara_gelinlik.repository.YoneticiRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class CreateTestYonetici {

    @Autowired
    private YoneticiRepository yoneticiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void createUserWithRoleUser() {
        Yonetici yonetici = new Yonetici();
        yonetici.setAd("Test");
        yonetici.setSoyad("User");
        yonetici.setEmail("user@test.com");
        yonetici.setSifre(passwordEncoder.encode("123456")); // şifreyi encode ediyoruz
        yonetici.setRole(Role.ROLE_USER); // ✅ enum tipi ile doğru kullanım

        yoneticiRepository.save(yonetici);
        System.out.println("Yeni yönetici oluşturuldu: " + yonetici.getEmail());
    }
}
