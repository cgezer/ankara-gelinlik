package com.ankara_gelinlik.entity;

import com.ankara_gelinlik.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "yonetici")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

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

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
