package com.ankara_gelinlik.repository;

import com.ankara_gelinlik.entity.Yonetici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YoneticiRepository extends JpaRepository<Yonetici, Long> {
    Optional<Yonetici> findByEmail(String email);
    boolean existsByEmail(String email);
}
