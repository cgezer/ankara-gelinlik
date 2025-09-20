package com.ankara_gelinlik.repository;

import com.ankara_gelinlik.entity.Yonetici;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class YoneticiRepositoryTest {

    @Autowired
    private YoneticiRepository yoneticiRepository;

    @Test
    void testCreateAndReadYonetici() {
        // CREATE
        Yonetici yonetici = new Yonetici();
        yonetici.setAd("Ahmet");
        yonetici.setSoyad("Yılmaz");
        yonetici.setEmail("ahmet@example.com");

        Yonetici saved = yoneticiRepository.save(yonetici);

        // READ
        Optional<Yonetici> found = yoneticiRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAd()).isEqualTo("Ahmet");
    }

    @Test
    void testUpdateYonetici() {
        Yonetici yonetici = new Yonetici();
        yonetici.setAd("Mehmet");
        yonetici.setSoyad("Kaya");
        yonetici.setEmail("mehmet@example.com");
        Yonetici saved = yoneticiRepository.save(yonetici);

        // UPDATE
        saved.setAd("Mert");
        Yonetici updated = yoneticiRepository.save(saved);

        assertThat(updated.getAd()).isEqualTo("Mert");
    }

    @Test
    void testDeleteYonetici() {
        Yonetici yonetici = new Yonetici();
        yonetici.setAd("Ayşe");
        yonetici.setSoyad("Demir");
        yonetici.setEmail("ayse@example.com");
        Yonetici saved = yoneticiRepository.save(yonetici);

        // DELETE
        yoneticiRepository.delete(saved);
        Optional<Yonetici> deleted = yoneticiRepository.findById(saved.getId());
        assertThat(deleted).isNotPresent();
    }
}
