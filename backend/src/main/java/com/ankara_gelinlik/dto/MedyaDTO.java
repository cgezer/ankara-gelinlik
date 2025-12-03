package com.ankara_gelinlik.dto;

import lombok.Data;

import java.util.Set;

@Data
public class MedyaDTO {
    private String baslik;
    private String aciklama;
    private String kategori;
    private Set<String> stiller;
    private Boolean yeniSezon;
    private Boolean indirimli;
    private Boolean aktif;
}
