package com.ankara_gelinlik.util;

import com.ankara_gelinlik.dto.YoneticiDTO;
import com.ankara_gelinlik.dto.UserDTO;
import org.apache.commons.text.StringEscapeUtils;

public class XSSUtil {

    // --- YoneticiDTO için XSS temizleme ---
    public static void sanitizeYoneticiDTO(YoneticiDTO dto) {
        if (dto == null) return;
        dto.setAd(sanitize(dto.getAd()));
        dto.setSoyad(sanitize(dto.getSoyad()));
        dto.setEmail(sanitize(dto.getEmail()));
    }

    // --- UserDTO için XSS temizleme ---
    public static void sanitizeUserDTO(UserDTO dto) {
        if (dto == null) return;
        dto.setAd(sanitize(dto.getAd()));
        dto.setSoyad(sanitize(dto.getSoyad()));
        dto.setEmail(sanitize(dto.getEmail()));
        dto.setRole(sanitize(dto.getRole()));
    }

    // --- Genel String temizleme ---
    public static String sanitize(String input) {
        if (input == null) return null;
        return StringEscapeUtils.escapeHtml4(input.trim());
    }
}
