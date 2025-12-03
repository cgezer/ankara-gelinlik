package com.ankara_gelinlik.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:ankara_gelinlik_secret_key_123456789012345678901234567890123456789012345678901234}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 saat
    private long jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            int needed = 64 - jwtSecret.getBytes(StandardCharsets.UTF_8).length;
            StringBuilder sb = new StringBuilder(jwtSecret);
            for (int i = 0; i < needed; i++) {
                sb.append("0");
            }
            jwtSecret = sb.toString();
        }
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Key getSigningKey() {
        return key;
    }

    // -----------------------------------------------------------------------
    // TOKEN OLUŞTURMA
    // -----------------------------------------------------------------------
    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateTokenFromEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", Collections.emptyList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // -----------------------------------------------------------------------
    // TOKEN OKUMA
    // -----------------------------------------------------------------------
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims c = parseClaims(token);

        Object rolesObj = c.get("roles");

        if (rolesObj instanceof List<?> rawList) {
            return rawList.stream()
                    .filter(o -> o instanceof String)
                    .map(o -> (String) o)
                    .toList();
        }
        return Collections.emptyList();
    }

    // -----------------------------------------------------------------------
    // TOKEN GEÇERLİLİK KONTROLÜ
    // -----------------------------------------------------------------------
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);

            // Expiration kontrolü
            return !claims.getExpiration().before(new Date());

        } catch (ExpiredJwtException ex) {
            System.out.println("JWT süresi geçmiş");
            return false;

        } catch (MalformedJwtException ex) {
            System.out.println("JWT yapısı bozuk");
            return false;

        } catch (UnsupportedJwtException ex) {
            System.out.println("JWT formatı desteklenmiyor");
            return false;

        } catch (SecurityException ex) {
            System.out.println("JWT imza doğrulaması başarısız");
            return false;

        } catch (IllegalArgumentException ex) {
            System.out.println("JWT boş ya da hatalı");
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // CLAIM PARSE EDİCİ – Cookie için güvenli
    // -----------------------------------------------------------------------
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(5) // clock drift toleransı
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
