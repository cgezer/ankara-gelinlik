package com.ankara_gelinlik.service;

import com.ankara_gelinlik.dto.MedyaDTO;
import com.ankara_gelinlik.entity.Medya;
import com.ankara_gelinlik.repository.MedyaRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedyaService {

    private final MedyaRepository medyaRepository;
    private static final Logger logger = LoggerFactory.getLogger(MedyaService.class);
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    public MedyaService(MedyaRepository medyaRepository) throws IOException {
        this.medyaRepository = medyaRepository;
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    public List<Medya> getAllMedya() {
        return medyaRepository.findAll();
    }

    public Optional<Medya> getMedyaById(Long id) {
        return medyaRepository.findById(id);
    }

    public Medya createMedya(MedyaDTO dto, MultipartFile file) throws IOException {
        Medya medya = new Medya();
        applyDtoToEntity(medya, dto);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Dosya gereklidir.");
        }

        saveFileToMedya(medya, file);
        medya.setOlusturmaTarihi(LocalDateTime.now());
        return medyaRepository.save(medya);
    }

    public Optional<Medya> updateMedya(Long id, MedyaDTO dto, MultipartFile file) throws IOException {
        return medyaRepository.findById(id).map(existing -> {
            applyDtoToEntity(existing, dto);

            if (file != null && !file.isEmpty()) {
                try {
                    // eski dosyayı sil
                    if (existing.getDosyaYolu() != null) {
                        Files.deleteIfExists(Paths.get(existing.getDosyaYolu()));
                    }
                    saveFileToMedya(existing, file);
                } catch (IOException e) {
                    logger.error("Dosya kaydedilemedi: {}", e.getMessage());
                    throw new RuntimeException("Dosya kaydetme hatası");
                }
            }
            return medyaRepository.save(existing);
        });
    }

    public void deleteMedya(Long id) {
        medyaRepository.findById(id).ifPresent(medya -> {
            try {
                if (medya.getDosyaYolu() != null) {
                    Files.deleteIfExists(Paths.get(medya.getDosyaYolu()));
                }
            } catch (IOException e) {
                logger.error("Dosya silinirken hata: {}", e.getMessage());
            }
            medyaRepository.deleteById(id);
        });
    }

    // ---------- yardımcı metodlar ----------

    private void applyDtoToEntity(Medya medya, MedyaDTO dto) {
        if (dto == null) return;

        medya.setBaslik(dto.getBaslik() != null ? StringEscapeUtils.escapeHtml4(dto.getBaslik()) : null);
        medya.setAciklama(dto.getAciklama() != null ? StringEscapeUtils.escapeHtml4(dto.getAciklama()) : null);
        medya.setKategori(dto.getKategori());
        medya.setStiller(dto.getStiller() != null ? dto.getStiller() : Collections.emptySet());
        medya.setYeniSezon(dto.getYeniSezon() != null ? dto.getYeniSezon() : Boolean.FALSE);
        medya.setIndirimli(dto.getIndirimli() != null ? dto.getIndirimli() : Boolean.FALSE);
        medya.setAktif(dto.getAktif() != null ? dto.getAktif() : Boolean.TRUE);
    }

    private void saveFileToMedya(Medya medya, MultipartFile file) throws IOException {
        String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();

        if (!isAllowedFileType(originalFilename, file.getContentType())) {
            throw new IllegalArgumentException("Geçersiz dosya türü: " + originalFilename);
        }

        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path targetLocation = uploadDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        medya.setDosyaAdi(uniqueFilename);
        medya.setDosyaYolu(targetLocation.toAbsolutePath().toString());
    }

    private boolean isAllowedFileType(String filename, String mimeType) {
        String lowercase = filename.toLowerCase();
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".mp4"};
        boolean validExt = false;
        for (String ext : allowedExtensions) if (lowercase.endsWith(ext)) { validExt = true; break; }

        String[] allowedMimes = {"image/jpeg", "image/png", "image/gif", "video/mp4"};
        boolean validMime = false;
        for (String mt : allowedMimes) if (mt.equalsIgnoreCase(mimeType)) { validMime = true; break; }

        return validExt && validMime;
    }
}
