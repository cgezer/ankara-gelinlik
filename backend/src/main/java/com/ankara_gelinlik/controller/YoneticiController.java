package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.YoneticiDTO;
import com.ankara_gelinlik.dto.UserDTO;
import com.ankara_gelinlik.service.YoneticiService;
import com.ankara_gelinlik.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/yonetici")
public class YoneticiController {

    private final YoneticiService yoneticiService;
    private final UserService userService;

    public YoneticiController(YoneticiService yoneticiService, UserService userService) {
        this.yoneticiService = yoneticiService;
        this.userService = userService;
    }

    // --- Listeleme ---
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("yoneticiler", yoneticiService.findAllDTO());
        model.addAttribute("kullanicilar", userService.findAllDTO());
        return "yonetici/list";
    }

    // --- Yeni ekleme formu ---
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("yoneticiDTO", new YoneticiDTO());
        return "yonetici/add";
    }

    // --- Yeni ekleme işlemi ---
    @PostMapping("/add")
    public String save(@ModelAttribute("yoneticiDTO") YoneticiDTO yoneticiDTO,
                       RedirectAttributes redirectAttributes) {
        try {
            switch (yoneticiDTO.getRole().toUpperCase()) {
                case "ROLE_ADMIN":
                    YoneticiDTO saved = yoneticiService.saveDTO(yoneticiDTO);
                    redirectAttributes.addFlashAttribute("successMessage", "Yeni yönetici başarıyla eklendi.");
                    redirectAttributes.addFlashAttribute("highlightId", saved.getId());
                    break;
                case "ROLE_USER":
                    UserDTO userDTO = mapToUserDTO(yoneticiDTO);
                    userService.saveDTO(userDTO);
                    redirectAttributes.addFlashAttribute("successMessage", "Yeni kullanıcı başarıyla eklendi.");
                    break;
                default:
                    throw new IllegalArgumentException("Geçersiz rol: " + yoneticiDTO.getRole());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kayıt eklenemedi: " + e.getMessage());
        }
        return "redirect:/yonetici/list";
    }

    private UserDTO mapToUserDTO(YoneticiDTO dto) {
        UserDTO userDTO = new UserDTO();
        userDTO.setAd(dto.getAd());
        userDTO.setSoyad(dto.getSoyad());
        userDTO.setEmail(dto.getEmail());
        userDTO.setSifre(dto.getSifre());
        userDTO.setRole("ROLE_USER");
        return userDTO;
    }

    // --- Güncelleme formu ---
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        YoneticiDTO dto = yoneticiService.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Yönetici bulunamadı: " + id));
        model.addAttribute("yoneticiDTO", dto);
        return "yonetici/edit";
    }

    // --- Güncelleme işlemi ---
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("yoneticiDTO") YoneticiDTO yoneticiDTO,
                         RedirectAttributes redirectAttributes) {
        try {
            yoneticiService.updateDTO(id, yoneticiDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Düzenleme işlemi başarılı.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Güncelleme başarısız: " + e.getMessage());
        }
        return "redirect:/yonetici/list";
    }

    // --- Silme işlemi ---
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            yoneticiService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Silme işlemi başarılı.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Silme başarısız: " + e.getMessage());
        }
        return "redirect:/yonetici/list";
    }

    // --- Şifre değiştirme formu ---
    @GetMapping("/change-password/{id}")
    public String changePasswordForm(@PathVariable Long id, Model model) {
        YoneticiDTO dto = yoneticiService.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Yönetici bulunamadı: " + id));
        model.addAttribute("yoneticiDTO", dto);
        return "yonetici/change-password";
    }

    // --- Şifre değiştirme işlemi ---
    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable Long id,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            YoneticiDTO dto = yoneticiService.findDTOById(id)
                    .orElseThrow(() -> new RuntimeException("Yönetici bulunamadı: " + id));
            yoneticiService.changePassword(dto.getEmail(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Şifre başarıyla değiştirildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Şifre değiştirilemedi: " + e.getMessage());
        }
        return "redirect:/yonetici/list";
    }

    // --- Kullanıcı güncelleme formu ---
    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        UserDTO dto = userService.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
        model.addAttribute("userDTO", dto);
        return "yonetici/edit-user";
    }

    // --- Kullanıcı güncelleme işlemi ---
    @PostMapping("/edit-user/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("userDTO") UserDTO userDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateDTO(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Kullanıcı düzenlendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kullanıcı düzenlenemedi: " + e.getMessage());
        }
        return "redirect:/yonetici/list";
    }

    // --- Kullanıcı silme işlemi ---
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kullanıcı silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kullanıcı silinemedi: " + e.getMessage());
        }
        return "redirect:/yonetici/list";
    }
}
