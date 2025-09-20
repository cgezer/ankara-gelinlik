package com.ankara_gelinlik.repository;

import com.ankara_gelinlik.entity.Yonetici;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YoneticiRepository extends JpaRepository<Yonetici, Long> {

    Optional<Yonetici> findByEmail(String email);
}
