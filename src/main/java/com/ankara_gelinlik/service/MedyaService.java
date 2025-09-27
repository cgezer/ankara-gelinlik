package com.ankara_gelinlik.service;

import com.ankara_gelinlik.dto.MedyaDTO;
import com.ankara_gelinlik.entity.Medya;
import com.ankara_gelinlik.repository.MedyaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedyaService {

    @Autowired
    private MedyaRepository medyaRepository;

    // Tüm medyaları listele
    public List<Medya> getAllMedya() {
        return medyaRepository.findAll();
    }

    // ID ile medya bul
    public Optional<Medya> getMedyaById(Long id) {
        return medyaRepository.findById(id);
    }

    // Yeni medya oluştur
    public Medya createMedya(MedyaDTO medyaDTO) {


        Medya medya = new Medya();
        medya.setBaslik(medyaDTO.getBaslik());
        medya.setAciklama(medyaDTO.getAciklama());
        medya.setDosyaAdi(medyaDTO.getDosyaAdi());
        medya.setDosyaYolu(medyaDTO.getDosyaYolu());
        medya.setOlusturmaTarihi(LocalDateTime.now());
        System.out.println("Kaydedilecek Medya: " + medya);
        return medyaRepository.save(medya);
    }

    // Mevcut medya güncelle
    public Optional<Medya> updateMedya(Long id, MedyaDTO medyaDTO) {
        return medyaRepository.findById(id).map(medya -> {
            medya.setBaslik(medyaDTO.getBaslik());
            medya.setAciklama(medyaDTO.getAciklama());
            medya.setDosyaAdi(medyaDTO.getDosyaAdi());
            medya.setDosyaYolu(medyaDTO.getDosyaYolu());
            return medyaRepository.save(medya);
        });
    }

    // Medya sil
    public void deleteMedya(Long id) {
        medyaRepository.deleteById(id);
    }
}
