package com.ankara_gelinlik.service;

import com.ankara_gelinlik.dto.YoneticiDTO;
import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.YoneticiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YoneticiService {

    private final YoneticiRepository yoneticiRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------------------------------
    // LOGIN
    // ---------------------------------
    public Yonetici validateLogin(String email, String password) {
        Yonetici user = yoneticiRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!passwordEncoder.matches(password, user.getSifre())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public Yonetici getUserByEmail(String email) {
        return yoneticiRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ---------------------------------
    // CRUD + DTO
    // ---------------------------------
    public List<YoneticiDTO> findAllDTO() {
        return yoneticiRepository.findAll()
                .stream()
                .map(YoneticiDTO::fromEntity)
                .toList();
    }

    public Optional<YoneticiDTO> findDTOById(Long id) {
        return yoneticiRepository.findById(id)
                .map(YoneticiDTO::fromEntity);
    }

    // ---------------------------------
    // CREATE (DTO)
    // ---------------------------------
    public YoneticiDTO saveDTO(YoneticiDTO dto) {

        // EMAIL CHECK (CREATE)
        if (yoneticiRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }

        if (dto.getSifre() == null || dto.getSifre().isBlank()) {
            throw new IllegalArgumentException("Şifre boş olamaz");
        }

        Yonetici user = dto.toEntity();
        user.setSifre(passwordEncoder.encode(dto.getSifre()));

        user = yoneticiRepository.save(user);
        return YoneticiDTO.fromEntity(user);
    }

    // ---------------------------------
    // UPDATE (DTO)
    // ---------------------------------
    public void updateDTO(Long id, YoneticiDTO dto) {

        Yonetici existing = yoneticiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // EMAIL CHECK (UPDATE)
        if (yoneticiRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new RuntimeException("EMAIL_ALREADY_EXISTS");
        }

        existing.setAd(dto.getAd());
        existing.setSoyad(dto.getSoyad());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());

        // Şifre boş gelmişse eski şifre korunacak
        if (dto.getSifre() != null && !dto.getSifre().isBlank()) {
            existing.setSifre(passwordEncoder.encode(dto.getSifre()));
        }

        yoneticiRepository.save(existing);
    }

    public void deleteById(Long id) {
        yoneticiRepository.deleteById(id);
    }
}
