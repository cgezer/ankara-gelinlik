package com.ankara_gelinlik.service;

import com.ankara_gelinlik.dto.UserDTO;
import com.ankara_gelinlik.dto.YoneticiDTO;
import com.ankara_gelinlik.entity.User;
import com.ankara_gelinlik.repository.UserRepository;
import com.ankara_gelinlik.util.XSSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Yeni kullanıcı kaydetme (YoneticiDTO'dan)
    public void saveUser(YoneticiDTO dto) {
        XSSUtil.sanitizeYoneticiDTO(dto);

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email boş olamaz");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email ile zaten bir kullanıcı kayıtlı: " + dto.getEmail());
        }

        User user = User.builder()
                .ad(dto.getAd())
                .soyad(dto.getSoyad())
                .email(dto.getEmail())
                .sifre(passwordEncoder.encode(dto.getSifre()))
                .role("ROLE_USER")
                .enabled(true)
                .build();

        userRepository.save(user);
        logger.info("Yeni kullanıcı kaydedildi (ROLE_USER): {}", dto.getEmail());
    }

    // --- DTO'dan kayıt (UserDTO versiyonu)
    public void saveDTO(UserDTO dto) {
        XSSUtil.sanitizeUserDTO(dto);

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email boş olamaz");
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email ile zaten bir kullanıcı kayıtlı: " + dto.getEmail());
        }

        User user = User.builder()
                .ad(dto.getAd())
                .soyad(dto.getSoyad())
                .email(dto.getEmail())
                .sifre(passwordEncoder.encode(dto.getSifre()))
                .role("ROLE_USER")
                .enabled(true)
                .build();

        userRepository.save(user);
        logger.info("Yeni kullanıcı kaydedildi (DTO): {}", dto.getEmail());
    }

    // --- Tüm kullanıcıları getir
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<UserDTO> findAllDTO() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Tek kullanıcı DTO bul
    public Optional<UserDTO> findDTOById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setAd(user.getAd());
        dto.setSoyad(user.getSoyad());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // --- Şifre değiştirme metodu
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(oldPassword, user.getSifre())) {
            throw new RuntimeException("Eski şifre yanlış!");
        }

        user.setSifre(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Kullanıcının şifresi başarıyla değiştirildi: {}", email);
    }

    // --- Kullanıcı güncelleme (DTO ile)
    public void updateDTO(Long id, UserDTO dto) {
        XSSUtil.sanitizeUserDTO(dto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

        // Eğer email değiştiyse ve başka kullanıcıda mevcutsa hata
        if (!user.getEmail().equals(dto.getEmail()) &&
                userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email ile zaten bir kullanıcı kayıtlı: " + dto.getEmail());
        }

        user.setAd(dto.getAd());
        user.setSoyad(dto.getSoyad());
        user.setEmail(dto.getEmail());

        // Şifre alanı boş değilse güncelle, boşsa mevcut şifreyi koru
        if (dto.getSifre() != null && !dto.getSifre().isBlank()) {
            user.setSifre(passwordEncoder.encode(dto.getSifre()));
        }

        userRepository.save(user);
        logger.info("Kullanıcı güncellendi: {}", dto.getEmail());
    }

    // --- Kullanıcı silme
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

        userRepository.delete(user);
        logger.info("Kullanıcı silindi: {}", user.getEmail());
    }
}
