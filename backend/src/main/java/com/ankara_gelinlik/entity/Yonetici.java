package com.ankara_gelinlik.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "yonetici") // DB'deki tablo adı: yonetici (tekil)
public class Yonetici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ad;
    private String soyad;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String sifre;

    @Column(nullable = false)
    private String role; // örn: "ADMIN" veya "ROLE_ADMIN" veya "USER"

    public Yonetici() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
