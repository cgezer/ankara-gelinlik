package com.ankara_gelinlik.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medya")
@Data
public class Medya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baslik;

    @Column(columnDefinition = "TEXT")
    private String aciklama;

    // Örnek: "Gelinlik", "Abiye", "Nisanlik", "Bindalli", "Kuafor", "Kozmetik"
    private String kategori;

    // Birden fazla stil seçilebilsin (ör: "tesettur", "acik", "kisa", "mezuniyet", ...)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medya_stiller", joinColumns = @JoinColumn(name = "medya_id"))
    @Column(name = "stil")
    private Set<String> stiller = new HashSet<>();

    private String dosyaAdi;
    private String dosyaYolu;

    private Boolean yeniSezon = false;
    private Boolean indirimli = false;
    private Boolean aktif = true;

    private LocalDateTime olusturmaTarihi;
}
