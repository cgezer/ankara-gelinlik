package com.ankara_gelinlik.service;

import com.ankara_gelinlik.entity.RefreshToken;
import com.ankara_gelinlik.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    // Token üretme / yenileme
    public RefreshToken createOrReuseRefreshToken(String email) {
        return refreshTokenRepository.findByEmailOrderByExpiryDateDesc(email)
                .stream()
                .findFirst()
                .map(existing -> {
                    if (existing.getExpiryDate().isAfter(Instant.now()))
                        return existing;

                    existing.setToken(UUID.randomUUID().toString());
                    existing.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
                    return refreshTokenRepository.save(existing);
                })
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken();
                    newToken.setEmail(email);
                    newToken.setToken(UUID.randomUUID().toString());
                    newToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
                    return refreshTokenRepository.save(newToken);
                });
    }

    public boolean validate(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(Instant.now()))
                .isPresent();
    }

    public String getEmailFromToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::getEmail)
                .orElse(null);
    }

    public void delete(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
