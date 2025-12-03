package com.ankara_gelinlik.dto;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.enums.Role;
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

    // ŞİFRE ARTIK UPDATE İÇİN ZORUNLU DEĞİL
    @Size(min = 3, max = 100, message = "Şifre en az 3 karakter olmalıdır")
    private String sifre;

    @NotBlank(message = "Rol boş olamaz")
    private Role role;

    // ---------------------------------
    // ENTITY → DTO
    // ---------------------------------
    public static YoneticiDTO fromEntity(Yonetici entity) {
        YoneticiDTO dto = new YoneticiDTO();
        dto.setId(entity.getId());
        dto.setAd(entity.getAd());
        dto.setSoyad(entity.getSoyad());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());
        // Şifre dönmez
        return dto;
    }

    // ---------------------------------
    // DTO → ENTITY
    // ---------------------------------
    public Yonetici toEntity() {
        Yonetici entity = new Yonetici();
        entity.setId(this.id);
        entity.setAd(this.ad);
        entity.setSoyad(this.soyad);
        entity.setEmail(this.email);
        entity.setSifre(this.sifre);
        entity.setRole(this.role);
        return entity;
    }

}
