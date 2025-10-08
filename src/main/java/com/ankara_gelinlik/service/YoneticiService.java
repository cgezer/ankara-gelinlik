package com.ankara_gelinlik.service;

import com.ankara_gelinlik.dto.YoneticiDTO;
import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.YoneticiRepository;
import com.ankara_gelinlik.util.XSSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class YoneticiService {

    private final YoneticiRepository yoneticiRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(YoneticiService.class);

    public YoneticiService(YoneticiRepository yoneticiRepository,
                           PasswordEncoder passwordEncoder,
                           UserService userService) {
        this.yoneticiRepository = yoneticiRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    // --- Entity list
    public List<Yonetici> findAll() {
        return yoneticiRepository.findAll();
    }

    // --- DTO list
    public List<YoneticiDTO> findAllDTO() {
        return yoneticiRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<YoneticiDTO> findDTOById(Long id) {
        return yoneticiRepository.findById(id).map(this::toDTO);
    }

    public Optional<Yonetici> findById(Long id) {
        return yoneticiRepository.findById(id);
    }

    public YoneticiDTO findDTOByEmail(String email) {
        return yoneticiRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
    }

    /**
     * Kullanıcı kaydetme:
     * - ROLE_ADMIN => yonetici tablosuna
     * - ROLE_USER  => users tablosuna (UserService)
     */
    public YoneticiDTO saveDTO(YoneticiDTO dto) {
        XSSUtil.sanitizeYoneticiDTO(dto);

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email boş olamaz");
        }
        if (dto.getSifre() == null || dto.getSifre().isBlank()) {
            throw new RuntimeException("Şifre boş olamaz");
        }

        // 🔒 Email çakışması kontrolü (her iki tabloda da)
        if (yoneticiRepository.findByEmail(dto.getEmail()).isPresent() ||
                userService.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email ile zaten bir kullanıcı mevcut: " + dto.getEmail());
        }

        // 🔹 Role bazlı yönlendirme
        if ("ROLE_USER".equalsIgnoreCase(dto.getRole())) {
            logger.info("Kullanıcı (ROLE_USER) kaydediliyor users tablosuna: {}", dto.getEmail());
            userService.saveUser(dto);
            return dto;
        }

        // 🔹 Aksi halde (ROLE_ADMIN) yöneticiyi kaydet
        Yonetici entity = toEntity(dto);
        entity.setSifre(passwordEncoder.encode(dto.getSifre()));
        Yonetici saved = yoneticiRepository.save(entity);
        logger.info("Yönetici kaydedildi (ROLE_ADMIN): {}", saved.getEmail());
        return toDTO(saved);
    }

    // --- Güncelleme
    public YoneticiDTO updateDTO(Long id, YoneticiDTO dto) {
        XSSUtil.sanitizeYoneticiDTO(dto);
        Yonetici entity = yoneticiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

        entity.setAd(dto.getAd());
        entity.setSoyad(dto.getSoyad());
        entity.setEmail(dto.getEmail());
        entity.setRole(dto.getRole());

        if (dto.getSifre() != null && !dto.getSifre().isBlank()) {
            entity.setSifre(passwordEncoder.encode(dto.getSifre()));
        }

        Yonetici saved = yoneticiRepository.save(entity);
        logger.info("Kullanıcı güncellendi: {}", saved.getEmail());
        return toDTO(saved);
    }

    // --- Silme
    public void deleteById(Long id) {
        if (!yoneticiRepository.existsById(id)) {
            throw new RuntimeException("Silinecek kullanıcı bulunamadı: " + id);
        }
        yoneticiRepository.deleteById(id);
        logger.info("Kullanıcı silindi: {}", id);
    }

    // --- Şifre değiştirme (email ile)
    public void changePassword(String email, String oldPassword, String newPassword) {
        Yonetici yonetici = yoneticiRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        if (!passwordEncoder.matches(oldPassword, yonetici.getSifre())) {
            throw new RuntimeException("Eski şifre hatalı");
        }

        yonetici.setSifre(passwordEncoder.encode(newPassword));
        yoneticiRepository.save(yonetici);
        logger.info("Şifre değiştirildi: {}", email);
    }

    public void changePasswordById(Long id, String yeniSifre) {
        YoneticiDTO dto = findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
        dto.setSifre(yeniSifre);
        updateDTO(id, dto);
        logger.info("Şifre değiştirildi: {}", dto.getEmail());
    }

    // --- Yardımcı dönüşümler
    private YoneticiDTO toDTO(Yonetici y) {
        if (y == null) return null;
        YoneticiDTO dto = new YoneticiDTO();
        dto.setId(y.getId());
        dto.setAd(y.getAd());
        dto.setSoyad(y.getSoyad());
        dto.setEmail(y.getEmail());
        dto.setRole(y.getRole());
        return dto;
    }

    private Yonetici toEntity(YoneticiDTO dto) {
        if (dto == null) return null;
        Yonetici y = new Yonetici();
        y.setAd(dto.getAd());
        y.setSoyad(dto.getSoyad());
        y.setEmail(dto.getEmail());
        y.setRole(dto.getRole());
        return y;
    }

    public Optional<Yonetici> findByEmail(String email) {
        return yoneticiRepository.findByEmail(email);
    }
}
