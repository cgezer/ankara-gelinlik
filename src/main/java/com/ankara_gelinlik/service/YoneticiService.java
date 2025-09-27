package com.ankara_gelinlik.service;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.YoneticiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YoneticiService {

    private final YoneticiRepository yoneticiRepository;

    public YoneticiService(YoneticiRepository yoneticiRepository) {
        this.yoneticiRepository = yoneticiRepository;
    }

    public Optional<Yonetici> findByEmail(String email) {
        return yoneticiRepository.findByEmail(email);
    }

    public List<Yonetici> findAll() {
        return yoneticiRepository.findAll();
    }

    public Yonetici save(Yonetici yonetici) {
        return yoneticiRepository.save(yonetici);
    }

    public boolean existsByEmail(String email) {
        return yoneticiRepository.existsByEmail(email);
    }
}
