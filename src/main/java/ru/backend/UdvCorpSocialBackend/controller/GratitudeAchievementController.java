package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.GratitudeAchievementCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.GratitudeAchievementDTO;
import ru.backend.UdvCorpSocialBackend.dto.GratitudeAchievementUpdateDTO;
import ru.backend.UdvCorpSocialBackend.model.enums.GaType;
import ru.backend.UdvCorpSocialBackend.service.GratitudeAchievementService;



import java.io.IOException;
import java.util.List;

@Tag(name = "Gratitude and Achievements", description = "API для управления благодарностями и достижениями сотрудников")
@RestController
@RequestMapping("/api")
public class GratitudeAchievementController {

    @Autowired
    private GratitudeAchievementService service;

    @Operation(summary = "Создать благодарность или достижение")
    @PostMapping(value = "/gratitude-achievements", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GratitudeAchievementDTO> createGratitudeAchievement(@Valid @ModelAttribute GratitudeAchievementCreateDTO dto) throws IOException {
        return ResponseEntity.status(201).body(service.createGratitudeAchievement(dto));
    }

    @Operation(summary = "Получить благодарность или достижение по ID")
    @GetMapping("/gratitude-achievements/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GratitudeAchievementDTO> getGratitudeAchievement(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.getGratitudeAchievement(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить список благодарностей/достижений")
    @GetMapping("/gratitude-achievements")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GratitudeAchievementDTO>> getAllGratitudeAchievements(
            @RequestParam(required = false) Integer senderId,
            @RequestParam(required = false) Integer receiverId,
            @RequestParam(required = false) GaType type) {
        return ResponseEntity.ok(service.getAllGratitudeAchievements(senderId, receiverId, type));
    }

    @Operation(summary = "Обновить благодарность или достижение")
    @PutMapping(value = "/gratitude-achievements/{id}", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GratitudeAchievementDTO> updateGratitudeAchievement(
            @PathVariable Integer id, @Valid @ModelAttribute GratitudeAchievementUpdateDTO dto) throws IOException {
        try {
            return ResponseEntity.ok(service.updateGratitudeAchievement(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    @Operation(summary = "Удалить благодарность или достижение")
    @DeleteMapping("/gratitude-achievements/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteGratitudeAchievement(@PathVariable Integer id) {
        try {
            service.deleteGratitudeAchievement(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @Operation(summary = "Получить отправленные благодарности/достижения текущего пользователя")
    @GetMapping("/employees/me/gratitude-achievements/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GratitudeAchievementDTO>> getSentGratitudeAchievements() {
        return ResponseEntity.ok(service.getSentGratitudeAchievements());
    }

    @Operation(summary = "Получить полученные благодарности/достижения текущего пользователя")
    @GetMapping("/employees/me/gratitude-achievements/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GratitudeAchievementDTO>> getReceivedGratitudeAchievements() {
        return ResponseEntity.ok(service.getReceivedGratitudeAchievements());
    }
}