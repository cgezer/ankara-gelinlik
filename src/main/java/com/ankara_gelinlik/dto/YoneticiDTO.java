package com.ankara_gelinlik.dto;

import com.ankara_gelinlik.entity.Yonetici;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoneticiDTO {

    private Long id;

    @NotBlank(message = "Ad boş olamaz")
    @Size(max = 50, message = "Ad 50 karakterden uzun olamaz")
    private String ad;

    @NotBlank(message = "Soyad boş olamaz")
    @Size(max = 50, message = "Soyad 50 karakterden uzun olamaz")
    private String soyad;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 3, max = 100, message = "Şifre en az 3 karakter olmalıdır")
    private String sifre;

    @NotBlank(message = "Rol boş olamaz")
    private String role;

    // --- Entity'den DTO'ya kolay dönüşüm ---
    public YoneticiDTO(Yonetici entity) {
        this.id = entity.getId();
        this.ad = entity.getAd();
        this.soyad = entity.getSoyad();
        this.email = entity.getEmail();
        this.sifre = entity.getSifre();
        this.role = entity.getRole();
    }
}
