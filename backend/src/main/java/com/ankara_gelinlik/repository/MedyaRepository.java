package com.ankara_gelinlik.repository;

import com.ankara_gelinlik.entity.Medya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedyaRepository extends JpaRepository<Medya, Long> {
}
