package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.MedyaDTO;
import com.ankara_gelinlik.entity.Medya;
import com.ankara_gelinlik.service.MedyaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medya")
public class MedyaController {

    @Autowired
    private MedyaService medyaService;

    // Tüm medyaları getir
    @GetMapping
    public List<Medya> getAllMedya() {
        return medyaService.getAllMedya();
    }

    // ID ile medya getir
    @GetMapping("/{id}")
    public ResponseEntity<Medya> getMedyaById(@PathVariable Long id) {
        return medyaService.getMedyaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Yeni medya oluştur
    @PostMapping
    public Medya createMedya(@RequestBody MedyaDTO medyaDTO) {
        System.out.println("POST geldi: " + medyaDTO); // 🔹 POST isteği logu
        Medya medya = medyaService.createMedya(medyaDTO);
        System.out.println("Kaydedilecek Medya: " + medya); // 🔹 DB kaydı logu
        return medya;
    }

    // Mevcut medya güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Medya> updateMedya(@PathVariable Long id, @RequestBody MedyaDTO medyaDTO) {
        return medyaService.updateMedya(id, medyaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Medya sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedya(@PathVariable Long id) {
        medyaService.deleteMedya(id);
        return ResponseEntity.noContent().build();
    }
}
