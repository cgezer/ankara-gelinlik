package com.ankara_gelinlik.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedyaDTO {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 255, message = "Başlık 255 karakterden uzun olamaz")
    private String baslik;

    @NotBlank(message = "Açıklama boş olamaz")
    @Size(max = 1000, message = "Açıklama 1000 karakterden uzun olamaz")
    private String aciklama;

    @Size(max = 255, message = "Dosya adı 255 karakterden uzun olamaz")
    private String dosyaAdi; // create sırasında MedyaService set ediyor

    @Size(max = 500, message = "Dosya yolu 500 karakterden uzun olamaz")
    private String dosyaYolu; // create sırasında MedyaService set ediyor
}
