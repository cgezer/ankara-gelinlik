package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.PasswordForm;
import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.service.YoneticiService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final YoneticiService yoneticiService;
    private final PasswordEncoder passwordEncoder;

    public UserController(YoneticiService yoneticiService, PasswordEncoder passwordEncoder) {
        this.yoneticiService = yoneticiService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("email", principal.getName());
        return "user/dashboard";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model, Principal principal) {
        model.addAttribute("email", principal.getName());
        model.addAttribute("passwordForm", new PasswordForm());
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordForm") PasswordForm form,
                                 Principal principal, Model model) {
        String email = principal.getName();
        Yonetici yonetici = yoneticiService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        if (!passwordEncoder.matches(form.getOldPassword(), yonetici.getSifre())) {
            model.addAttribute("error", "Mevcut şifreniz yanlış.");
            return "user/change-password";
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Yeni şifre ile tekrar şifre eşleşmiyor.");
            return "user/change-password";
        }

        yonetici.setSifre(passwordEncoder.encode(form.getNewPassword()));
        yoneticiService.save(yonetici);

        model.addAttribute("success", "Şifre başarıyla değiştirildi.");
        return "user/change-password";
    }
}
