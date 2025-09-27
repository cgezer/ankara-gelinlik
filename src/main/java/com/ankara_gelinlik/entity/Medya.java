package com.ankara_gelinlik.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "medya")
@Data
public class Medya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baslik;
    private String aciklama;
    private String dosyaAdi;
    private String dosyaYolu;

    private LocalDateTime olusturmaTarihi;
}
