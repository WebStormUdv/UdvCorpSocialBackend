package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.subdivision.*;
import ru.backend.UdvCorpSocialBackend.service.SubdivisionService;

import java.util.List;

@RestController
@RequestMapping("/api/subdivisions")
@Tag(name = "Subdivisions", description = "API для управления подразделениями")
public class SubdivisionController {

    @Autowired
    private SubdivisionService subdivisionService;

    @Operation(
            summary = "Получить все подразделения",
            description = "Возвращает список всех подразделений, с возможностью фильтрации по ID департамента. Доступно для всех аутентифицированных пользователей."
    )
    @GetMapping
    public ResponseEntity<List<SubdivisionResponseDTO>> getAllSubdivisions(@RequestParam(required = false) Integer departmentId) {
        return ResponseEntity.ok(subdivisionService.getAllSubdivisions(departmentId));
    }

    @Operation(
            summary = "Получить подразделение по ID",
            description = "Возвращает подробную информацию о подразделении по его ID. Доступно для всех аутентифицированных пользователей."
    )
    @GetMapping("/{id}")
    public ResponseEntity<SubdivisionResponseDTO> getSubdivisionById(@PathVariable Integer id) {
        return ResponseEntity.ok(subdivisionService.getSubdivisionById(id));
    }

    @Operation(
            summary = "Создать новое подразделение",
            description = "Создает новое подразделение с указанными данными. Доступно только пользователям с ролью ADMIN."
    )
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SubdivisionResponseDTO> createSubdivision(@RequestBody SubdivisionCreateDTO subdivisionDTO) {
        return ResponseEntity.ok(subdivisionService.createSubdivision(subdivisionDTO));
    }

    @Operation(
            summary = "Обновить подразделение",
            description = "Обновляет существующее подразделение по его ID. Доступно только пользователям с ролью ADMIN."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SubdivisionResponseDTO> updateSubdivision(@PathVariable Integer id, @RequestBody SubdivisionCreateDTO subdivisionDTO) {
        return ResponseEntity.ok(subdivisionService.updateSubdivision(id, subdivisionDTO));
    }

    @Operation(
            summary = "Удалить подразделение",
            description = "Удаляет подразделение по его ID, если с ним не связаны сотрудники. Доступно только пользователям с ролью ADMIN."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteSubdivision(@PathVariable Integer id) {
        subdivisionService.deleteSubdivision(id);
        return ResponseEntity.noContent().build();
    }
}