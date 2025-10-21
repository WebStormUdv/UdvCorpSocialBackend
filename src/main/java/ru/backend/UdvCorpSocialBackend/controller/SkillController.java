package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillCreateDto;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillDTO;
import ru.backend.UdvCorpSocialBackend.service.SkillService;

import java.util.List;

@Tag(name = "Skills", description = "API для управления навыками сотрудников")
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Operation(
            summary = "Получение списка всех навыков",
            description = "Возвращает список всех навыков, доступных в системе, включая их идентификатор, название, тип и описания уровней владения (от Intern до Senior)."
    )
    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @Operation(
            summary = "Получение информации о навыке по ID",
            description = "Возвращает подробную информацию о навыке по его идентификатору, включая название, тип и описания уровней владения (например, для JavaScript: описание для уровней 1–5)."
    )
    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable Integer id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @Operation(
            summary = "Создание нового навыка",
            description = "Создает новый навык в системе. Требуется роль администратора. В запросе передаются название, тип (например, 'technical', 'soft skills') и описания уровней владения. Навык сохраняется в базе данных, и возвращается его полное описание, включая сгенерированный ID."
    )
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SkillDTO> createSkill(@RequestBody SkillCreateDto skillDTO) {
        return ResponseEntity.status(201).body(skillService.createSkill(skillDTO));
    }

    @Operation(
            summary = "Обновление существующего навыка",
            description = "Обновляет данные навыка по указанному ID. Требуется роль администратора. В запросе передаются новое название, тип и обновленные описания уровней владения. Если навык с таким названием уже существует, будет возвращена ошибка."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SkillDTO> updateSkill(@PathVariable Integer id, @RequestBody SkillCreateDto skillDTO) {
        return ResponseEntity.ok(skillService.updateSkill(id, skillDTO));
    }

    @Operation(
            summary = "Удаление навыка",
            description = "Удаляет навык из системы по указанному ID. Требуется роль администратора. Удаление возможно только если навык не связан с профилями сотрудников в таблице Employee_Skills, чтобы предотвратить нарушение целостности данных."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}