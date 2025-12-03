package com.ankara_gelinlik.security;

import com.ankara_gelinlik.entity.User;
import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.repository.UserRepository;
import com.ankara_gelinlik.repository.YoneticiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        // 🔹 Yönetici kontrolü
        Yonetici yonetici = yoneticiRepository.findByEmail(email).orElse(null);
        if (yonetici != null) {
            logger.info("Yönetici bulundu: {}", yonetici.getEmail());
            return new CustomUserDetails(yonetici);
        }

        // 🔹 Normal kullanıcı kontrolü
        User kullanici = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Kullanıcı bulunamadı: {}", email);
                    return new UsernameNotFoundException("Kullanıcı bulunamadı: " + email);
                });

        logger.info("Normal kullanıcı bulundu: {}", kullanici.getEmail());
        return new CustomUserDetails(kullanici);
    }
}
