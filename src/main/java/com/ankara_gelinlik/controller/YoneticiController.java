package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.entity.Yonetici;
import com.ankara_gelinlik.service.YoneticiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/yonetici")
public class YoneticiController {

    private final YoneticiService service;

    public YoneticiController(YoneticiService service) {
        this.service = service;
    }

    // Tüm yöneticileri listeleme
    @GetMapping
    public String list(Model model) {
        model.addAttribute("yoneticiler", service.getAllYonetici());
        return "yonetici/list"; // Thymeleaf template
    }

    // Yeni yönetici ekleme formu
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("yonetici", new Yonetici());
        return "yonetici/create";
    }

    // Yeni yönetici kaydetme
    @PostMapping("/create")
    public String createSubmit(@ModelAttribute Yonetici yonetici) {
        service.createYonetici(yonetici);
        return "redirect:/yonetici";
    }

    // Yönetici düzenleme formu
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("yonetici", service.getYoneticiById(id));
        return "yonetici/edit";
    }

    // Yönetici güncelleme işlemi
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id, @ModelAttribute Yonetici yonetici) {
        service.updateYonetici(id, yonetici);
        return "redirect:/yonetici";
    }

    // Yönetici silme
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteYonetici(id);
        return "redirect:/yonetici";
    }
}
