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
import java.util.List;
import java.util.Optional;

@Service
public class MedyaService {

    private final MedyaRepository medyaRepository;
    private static final Logger logger = LoggerFactory.getLogger(MedyaService.class);
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    public MedyaService(MedyaRepository medyaRepository) throws IOException {
        this.medyaRepository = medyaRepository;
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
    }

    public List<Medya> getAllMedya() { return medyaRepository.findAll(); }

    public Optional<Medya> getMedyaById(Long id) { return medyaRepository.findById(id); }

    public Medya createMedya(MedyaDTO medyaDTO, MultipartFile file) throws IOException {
        String baslik = StringEscapeUtils.escapeHtml4(medyaDTO.getBaslik());
        String aciklama = StringEscapeUtils.escapeHtml4(medyaDTO.getAciklama());

        String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
        if (!isAllowedFileType(originalFilename, file.getContentType()))
            throw new IllegalArgumentException("Geçersiz dosya türü: " + originalFilename);

        Path targetLocation = uploadDir.resolve(originalFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Medya medya = new Medya();
        medya.setBaslik(baslik);
        medya.setAciklama(aciklama);
        medya.setDosyaAdi(originalFilename);
        medya.setDosyaYolu(targetLocation.toAbsolutePath().toString());
        medya.setOlusturmaTarihi(LocalDateTime.now());

        return medyaRepository.save(medya);
    }

    public Optional<Medya> updateMedya(Long id, MedyaDTO medyaDTO) {
        return medyaRepository.findById(id).map(medya -> {
            medya.setBaslik(StringEscapeUtils.escapeHtml4(medyaDTO.getBaslik()));
            medya.setAciklama(StringEscapeUtils.escapeHtml4(medyaDTO.getAciklama()));
            if (medyaDTO.getDosyaAdi() != null && medyaDTO.getDosyaYolu() != null) {
                medya.setDosyaAdi(medyaDTO.getDosyaAdi());
                medya.setDosyaYolu(medyaDTO.getDosyaYolu());
            }
            return medyaRepository.save(medya);
        });
    }

    public void deleteMedya(Long id) {
        medyaRepository.findById(id).ifPresent(medya -> {
            try { Files.deleteIfExists(Paths.get(medya.getDosyaYolu())); }
            catch (IOException e) { logger.error("Dosya silinirken hata: {}", e.getMessage()); }
            medyaRepository.deleteById(id);
        });
    }

    private boolean isAllowedFileType(String filename, String mimeType) {
        String lowercase = filename.toLowerCase();
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".mp4"};
        boolean validExt = false;
        for (String ext : allowedExtensions) if (lowercase.endsWith(ext)) { validExt = true; break; }

        String[] allowedMimes = {"image/jpeg","image/png","image/gif","video/mp4"};
        boolean validMime = false;
        for (String mt : allowedMimes) if (mt.equalsIgnoreCase(mimeType)) { validMime = true; break; }

        return validExt && validMime;
    }
}
