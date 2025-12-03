package com.ankara_gelinlik.controller;

import com.ankara_gelinlik.dto.YoneticiDTO;
import com.ankara_gelinlik.service.YoneticiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yonetici")
@RequiredArgsConstructor
public class YoneticiController {

    private final YoneticiService yoneticiService;

    @GetMapping("/users")
    public List<YoneticiDTO> getAllUsers() {
        return yoneticiService.findAllDTO();
    }

    @GetMapping("/users/{id}")
    public YoneticiDTO getUserById(@PathVariable Long id) {
        return yoneticiService.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Yönetici bulunamadı"));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody YoneticiDTO dto) {
        try {
            return ResponseEntity.ok(yoneticiService.saveDTO(dto));
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("EMAIL_ALREADY_EXISTS")) {
                return ResponseEntity.status(409).body("Email already in use");
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody YoneticiDTO dto) {
        try {
            yoneticiService.updateDTO(id, dto);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("EMAIL_ALREADY_EXISTS")) {
                return ResponseEntity.status(409).body("Email already in use");
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        yoneticiService.deleteById(id);
    }
}
