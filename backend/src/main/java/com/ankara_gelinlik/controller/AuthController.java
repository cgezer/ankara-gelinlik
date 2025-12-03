package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.LoginRequest;
import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.security.JwtTokenProvider;
import com.ankara_gelinlik.service.RefreshTokenService;
import com.ankara_gelinlik.service.YoneticiService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final YoneticiService yoneticiService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    private final boolean IS_PROD = false; // LOCAL → false, PROD → true

    // Login işlemi
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        Yonetici user = yoneticiService.validateLogin(req.getEmail(), req.getPassword());

        // Access token oluşturuluyor
        String accessToken = jwtTokenProvider.generateTokenFromEmail(user.getEmail());

        // Refresh token oluşturuluyor veya varsa geri alınır
        var refresh = refreshTokenService.createOrReuseRefreshToken(user.getEmail());

        // Response'da cookie olarak gönderiyoruz
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(false)              // Prod ortamında true yap, local'de false
                .path("/")
                .maxAge(60 * 15)               // 15 dakika geçerli
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)     // 7 gün geçerli
                .sameSite("Lax")
                .build();

        // Cookie'leri header'a ekliyoruz
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    // Refresh token ile access token yenileme işlemi
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[]{})
                .filter(c -> c.getName().equals("refresh_token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (token == null || !refreshTokenService.validate(token))
            return ResponseEntity.status(401).body("Invalid refresh token");

        String email = refreshTokenService.getEmailFromToken(token);
        String newAccessToken = jwtTokenProvider.generateTokenFromEmail(email);

        // Yeni access token'ı cookie olarak gönderiyoruz
        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)               // 15 dakika geçerli
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ResponseEntity.ok(Map.of("status", "refreshed"));
    }

    // Kullanıcı bilgilerini almak için /me endpoint
    @GetMapping("/me")
    public ResponseEntity<?> me(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(value = "access_token", required = false) String tokenCookie) {

        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : tokenCookie;

        if (token == null || !jwtTokenProvider.validateToken(token))
            return ResponseEntity.status(401).build();

        String email = jwtTokenProvider.getEmailFromToken(token);
        var user = yoneticiService.getUserByEmail(email);

        return ResponseEntity.ok(Map.of(
                "ad", user.getAd(),
                "soyad", user.getSoyad(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }


    // Logout işlemi
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Access token ve refresh token cookie'lerini sıfırlıyoruz
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)   // Token süresi sıfırlanır
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().body("Logged out.");
    }
}
