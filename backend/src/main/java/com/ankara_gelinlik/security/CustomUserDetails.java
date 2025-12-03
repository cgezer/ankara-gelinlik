package com.ankara_gelinlik.security;

import com.ankara_gelinlik.entity.User;
import com.ankara_gelinlik.entity.Yonetici;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String sifre;
    private final String role; // DB'den gelen ham role (ör. "ROLE_ADMIN" veya "ADMIN")

    // Yonetici için
    public CustomUserDetails(Yonetici yonetici) {
        this.email = yonetici.getEmail();
        this.sifre = yonetici.getSifre();
        this.role = yonetici.getRole() != null ? yonetici.getRole().name() : null;
    }

    // User için
    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.sifre = user.getSifre();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*
         * Güvenli ve uyumlu davranış:
         * - Eğer DB'de "ROLE_ADMIN" varsa, hem "ROLE_ADMIN" hem de "ADMIN" authority ekle.
         * - Eğer DB'de "ADMIN" varsa, hem "ADMIN" hem de "ROLE_ADMIN" ekle.
         * Böylece hasRole("ADMIN"), hasAuthority("ROLE_ADMIN") veya hasAuthority("ADMIN") hepsi çalışır.
         */
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role == null || role.isBlank()) {
            return authorities;
        }

        String normalized = role.trim();

        if (normalized.startsWith("ROLE_")) {
            authorities.add(new SimpleGrantedAuthority(normalized)); // ROLE_ADMIN
            String withoutPrefix = normalized.substring("ROLE_".length());
            if (!withoutPrefix.isBlank()) {
                authorities.add(new SimpleGrantedAuthority(withoutPrefix)); // ADMIN
            }
        } else {
            authorities.add(new SimpleGrantedAuthority(normalized)); // ADMIN
            authorities.add(new SimpleGrantedAuthority("ROLE_" + normalized)); // ROLE_ADMIN
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return sifre;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // equals/hashCode helpers (optional but useful)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
