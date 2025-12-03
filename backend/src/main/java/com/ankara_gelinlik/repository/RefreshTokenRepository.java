package com.ankara_gelinlik.repository;

import com.ankara_gelinlik.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByEmailOrderByExpiryDateDesc(String email);

    void deleteAllByExpiryDateBefore(Instant when);

    void deleteByToken(String token);

    void deleteAllByEmail(String email);
}
