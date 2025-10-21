package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.department.*;
import ru.backend.UdvCorpSocialBackend.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "API для управления отделами")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Operation(
            summary = "Получить все отделы",
            description = "Возвращает список всех отделов. Доступно для всех аутентифицированных пользователей."
    )
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @Operation(
            summary = "Получить департамент по ID",
            description = "Возвращает подробную информацию о департаменте по его ID. Доступно для всех аутентифицированных пользователей."
    )
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @Operation(
            summary = "Создать новый департамент",
            description = "Создает новый департамент с указанными данными. Доступно только пользователям с ролью ADMIN."
    )
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@RequestBody DepartmentCreateDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.createDepartment(departmentDTO));
    }

    @Operation(
            summary = "Обновить департамент",
            description = "Обновляет существующий департамент по его ID. Доступно только пользователям с ролью ADMIN."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Integer id, @RequestBody DepartmentCreateDTO departmentDTO) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, departmentDTO));
    }

    @Operation(
            summary = "Удалить департамент",
            description = "Удаляет департамент по его ID, если с ним не связаны сотрудники или подразделения. Доступно только пользователям с ролью ADMIN."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}