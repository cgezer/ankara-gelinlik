package com.ankara_gelinlik.service;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.YoneticiRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YoneticiService {

    private final YoneticiRepository repository;
    private final PasswordEncoder passwordEncoder;

    public YoneticiService(YoneticiRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Yonetici> getAllYonetici() {
        return repository.findAll();
    }

    public Yonetici getYoneticiById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Yönetici bulunamadı"));
    }

    public Yonetici createYonetici(Yonetici yonetici) {
        // Şifreyi encode et
        yonetici.setSifre(passwordEncoder.encode(yonetici.getSifre()));
        return repository.save(yonetici);
    }

    public Yonetici updateYonetici(Long id, Yonetici yonetici) {
        Yonetici existing = getYoneticiById(id);
        existing.setAd(yonetici.getAd());
        existing.setSoyad(yonetici.getSoyad());
        existing.setEmail(yonetici.getEmail());

        // Eğer şifre alanı doluysa encode ederek güncelle
        if (yonetici.getSifre() != null && !yonetici.getSifre().isEmpty()) {
            existing.setSifre(passwordEncoder.encode(yonetici.getSifre()));
        }

        return repository.save(existing);
    }

    public void deleteYonetici(Long id) {
        repository.deleteById(id);
    }
}
