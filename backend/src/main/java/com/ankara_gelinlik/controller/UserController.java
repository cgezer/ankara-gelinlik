package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.entity.User;
import com.ankara_gelinlik.dto.UserDTO;
import com.ankara_gelinlik.repository.UserRepository;
import com.ankara_gelinlik.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auth/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository,
                          UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // =====================
    // Thymeleaf sayfaları
    // =====================

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        String email = userDetails.getUsername();
        try {
            userService.changePassword(email, oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Şifreniz başarıyla değiştirildi.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        UserDTO dto = userService.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
        model.addAttribute("userDTO", dto);
        return "user/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("userDTO") UserDTO userDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateDTO(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Kullanıcı düzenlendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kullanıcı düzenlenemedi: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("userDTO") UserDTO userDTO,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.saveDTO(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Yeni kullanıcı başarıyla eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kullanıcı eklenemedi: " + e.getMessage());
        }
        return "redirect:/user/list";
    }

    // =====================
    // REST Endpoints (React için)
    // =====================

    @GetMapping("/api/users")
    @ResponseBody
    public List<UserDTO> getAllUsers() {
        return userService.findAllDTO();
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
    }

    @PostMapping("/api/users")
    @ResponseBody
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        userService.saveDTO(userDTO);
        return userDTO; // saveDTO void olduğu için, eklenen objeyi kendimiz return ediyoruz
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public UserDTO updateUserApi(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userService.updateDTO(id, userDTO);
        return userDTO;
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public void deleteUserApi(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
