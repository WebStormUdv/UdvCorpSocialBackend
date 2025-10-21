package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillSuggestionCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillSuggestionDTO;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillSuggestionUpdateDTO;
import ru.backend.UdvCorpSocialBackend.service.SkillSuggestionService;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Skill Suggestions", description = "API для управления предложениями новых навыков")
@RestController
@RequestMapping("/api/skill-suggestions")
public class SkillSuggestionController {

    @Autowired
    private SkillSuggestionService skillSuggestionService;

    @Operation(
            summary = "Создание предложения нового навыка",
            description = "Позволяет любому сотруднику предложить новый навык для добавления в систему. В запросе указываются название и тип навыка. Предложение получает статус 'pending'."
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SkillSuggestionDTO> createSkillSuggestion(@Valid @RequestBody SkillSuggestionCreateDTO dto) {
        return ResponseEntity.status(201).body(skillSuggestionService.createSkillSuggestion(dto));
    }

    @Operation(
            summary = "Получение списка всех предложений навыков",
            description = "Возвращает список всех предложений навыков, включая их ID, название, тип, статус и информацию о предложившем сотруднике. Доступно только для HR и администраторов."
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<List<SkillSuggestionDTO>> getAllSkillSuggestions() {
        return ResponseEntity.ok(skillSuggestionService.getAllSkillSuggestions());
    }

    @Operation(
            summary = "Утверждение или отклонение предложения навыка",
            description = "Позволяет HR или администратору утвердить или отклонить предложение навыка. При утверждении создается новый навык в таблице Skills. В запросе указывается статус ('approved' или 'rejected'), ID утверждающего и дата утверждения."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<SkillSuggestionDTO> updateSkillSuggestion(@PathVariable Integer id, @Valid @RequestBody SkillSuggestionUpdateDTO dto) {
        return ResponseEntity.ok(skillSuggestionService.updateSkillSuggestion(id, dto));
    }
}