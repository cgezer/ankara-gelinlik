package com.ankara_gelinlik.security;

import com.ankara_gelinlik.entity.User;
import com.ankara_gelinlik.entity.Yonetici;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String sifre;
    private final String role;

    // Yonetici için
    public CustomUserDetails(Yonetici yonetici) {
        this.email = yonetici.getEmail();
        this.sifre = yonetici.getSifre();
        this.role = yonetici.getRole();
    }

    // User için
    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.sifre = user.getSifre();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role başına ROLE_ prefix eklenebilir
        String rolePrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return Collections.singleton(new SimpleGrantedAuthority(rolePrefix));
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
}
