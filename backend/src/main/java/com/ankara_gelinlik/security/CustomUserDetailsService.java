package com.ankara_gelinlik.security;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.entity.User;
import com.ankara_gelinlik.repository.YoneticiRepository;
import com.ankara_gelinlik.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final YoneticiRepository yoneticiRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(YoneticiRepository yoneticiRepository,
                                    UserRepository userRepository) {
        this.yoneticiRepository = yoneticiRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        logger.info("Kullanıcı giriş denemesi: {}", email);

        // 🔹 1. Önce yöneticiler tablosuna bak
        Yonetici yonetici = yoneticiRepository.findByEmail(email).orElse(null);
        if (yonetici != null) {
            logger.info("Yönetici bulundu: {}", yonetici.getEmail());

            String role = yonetici.getRole();
            if (role == null || role.isBlank()) {
                role = "ROLE_ADMIN";
            }

            UserBuilder builder = org.springframework.security.core.userdetails.User
                    .withUsername(yonetici.getEmail())
                    .password(yonetici.getSifre())
                    .roles(role.replace("ROLE_", "")); // ROLE_ önekini iki kez yazmamak için
            return builder.build();
        }

        // 🔹 2. Eğer yönetici değilse, users tablosuna bak
        User kullanici = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Kullanıcı bulunamadı: {}", email);
                    return new UsernameNotFoundException("Kullanıcı bulunamadı: " + email);
                });

        logger.info("Normal kullanıcı bulundu: {}", kullanici.getEmail());

        String role = kullanici.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }

        UserBuilder builder = org.springframework.security.core.userdetails.User
                .withUsername(kullanici.getEmail())
                .password(kullanici.getSifre())
                .roles(role.replace("ROLE_", "")); // ROLE_USER → USER

        return builder.build();
    }
}
