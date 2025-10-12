package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.MedyaDTO;
import com.ankara_gelinlik.entity.Medya;
import com.ankara_gelinlik.service.MedyaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/medya")
public class MedyaController {

    private final MedyaService medyaService;
    private static final Logger logger = LoggerFactory.getLogger(MedyaController.class);

    public MedyaController(MedyaService medyaService) {
        this.medyaService = medyaService;
    }

    @GetMapping
    public ResponseEntity<List<Medya>> getAllMedya() {
        return ResponseEntity.ok(medyaService.getAllMedya());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medya> getMedyaById(@PathVariable Long id) {
        return medyaService.getMedyaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMedya(
            @RequestParam("baslik") String baslik,
            @RequestParam("aciklama") String aciklama,
            @RequestPart("file") MultipartFile file) throws IOException {

        MedyaDTO medyaDTO = new MedyaDTO();
        medyaDTO.setBaslik(baslik);
        medyaDTO.setAciklama(aciklama);

        Medya medya = medyaService.createMedya(medyaDTO, file);
        return ResponseEntity.ok(medya);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedya(
            @PathVariable Long id,
            @RequestBody @Valid MedyaDTO medyaDTO) {
        return medyaService.updateMedya(id, medyaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedya(@PathVariable Long id) {
        medyaService.deleteMedya(id);
        return ResponseEntity.noContent().build();
    }
}

