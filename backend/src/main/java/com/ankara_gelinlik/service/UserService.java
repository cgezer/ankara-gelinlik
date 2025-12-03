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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Kullanıcı ekleme (DTO ya da YoneticiDTO'dan) ---
    public void saveUser(YoneticiDTO dto) {
        XSSUtil.sanitizeYoneticiDTO(dto);
        saveUserInternal(dto.getAd(), dto.getSoyad(), dto.getEmail(), dto.getSifre(), "ROLE_USER");
    }

    public void saveDTO(UserDTO dto) {
        XSSUtil.sanitizeUserDTO(dto);
        saveUserInternal(dto.getAd(), dto.getSoyad(), dto.getEmail(), dto.getSifre(), "ROLE_USER");
    }

    private void saveUserInternal(String ad, String soyad, String email, String sifre, String role) {
        if (email == null || email.isBlank()) throw new RuntimeException("Email boş olamaz");
        if (sifre == null || sifre.isBlank()) throw new RuntimeException("Şifre boş olamaz");

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Bu email ile zaten bir kullanıcı kayıtlı: " + email);
        }

        User user = User.builder()
                .ad(ad)
                .soyad(soyad)
                .email(email)
                .sifre(passwordEncoder.encode(sifre))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(user);
        logger.info("Yeni kullanıcı kaydedildi: {}", email);
    }

    // --- Okuma işlemleri ---
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<UserDTO> findAllDTO() {
        logger.info("Tüm kullanıcılar DB'den çekiliyor...");
        List<User> list = userRepository.findAll();
        if (list.isEmpty()) {
            logger.warn("User tablosu boş!");
        }
        return list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findDTOById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // --- Şifre değiştir ---
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        if (!passwordEncoder.matches(oldPassword, user.getSifre())) {
            throw new RuntimeException("Eski şifre hatalı!");
        }

        user.setSifre(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Kullanıcının şifresi güncellendi: {}", email);
    }

    // --- Güncelle ---
    public void updateDTO(Long id, UserDTO dto) {
        XSSUtil.sanitizeUserDTO(dto);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

        if (!user.getEmail().equals(dto.getEmail()) &&
                userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email ile zaten bir kullanıcı kayıtlı: " + dto.getEmail());
        }

        user.setAd(dto.getAd());
        user.setSoyad(dto.getSoyad());
        user.setEmail(dto.getEmail());
        if (dto.getSifre() != null && !dto.getSifre().isBlank()) {
            user.setSifre(passwordEncoder.encode(dto.getSifre()));
        }

        userRepository.save(user);
        logger.info("Kullanıcı güncellendi: {}", dto.getEmail());
    }

    // --- Sil ---
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Silinecek kullanıcı bulunamadı: " + id);
        }
        userRepository.deleteById(id);
        logger.info("Kullanıcı silindi (ID: {})", id);
    }

    // --- Yardımcı DTO dönüştürme ---
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setAd(user.getAd());
        dto.setSoyad(user.getSoyad());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
    // UserService.java içine ekle
    public Optional<UserDTO> findDTOByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDTO);
    }

}
