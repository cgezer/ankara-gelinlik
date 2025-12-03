package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.MedyaDTO;
import com.ankara_gelinlik.entity.Medya;
import com.ankara_gelinlik.service.MedyaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auth/medya")
public class MedyaController {

    private final MedyaService medyaService;
    private final ObjectMapper objectMapper;

    public MedyaController(MedyaService medyaService, ObjectMapper objectMapper) {
        this.medyaService = medyaService;
        this.objectMapper = objectMapper;
    }

    // ✅ ADMIN → JWT ister
    @GetMapping
    public ResponseEntity<List<Medya>> getAllMedya() {
        return ResponseEntity.ok(medyaService.getAllMedya());
    }

    // ✅ ADMIN → JWT ister
    @GetMapping("/{id}")
    public ResponseEntity<Medya> getMedyaById(@PathVariable Long id) {
        return medyaService.getMedyaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ ADMIN → JWT ister / Create — multipart: metadata + file
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMedya(
            @RequestParam("medya") String medyaJson,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        // JSON string → DTO
        MedyaDTO medyaDTO = objectMapper.readValue(medyaJson, MedyaDTO.class);

        Medya created = medyaService.createMedya(medyaDTO, file);
        return ResponseEntity.ok(created);
    }

    // ✅ ADMIN → JWT ister / Update — multipart metadata + optional new file
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMedya(
            @PathVariable Long id,
            @RequestParam("medya") String medyaJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        MedyaDTO medyaDTO = objectMapper.readValue(medyaJson, MedyaDTO.class);

        return medyaService.updateMedya(id, medyaDTO, file)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ ADMIN → JWT ister / Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedya(@PathVariable Long id) {
        medyaService.deleteMedya(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ PUBLIC → GİRİŞ İSTEMEZ (frontend slider / galeri için)
    @GetMapping("/public/all")
    public ResponseEntity<List<Medya>> getAllMedyaPublic() {
        return ResponseEntity.ok(medyaService.getAllMedya());
    }
}
