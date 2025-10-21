package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeProfileDto;
import ru.backend.UdvCorpSocialBackend.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Profiles", description = "API для управления профилями сотрудников")
public class EmployeeProfileController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeProfileController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(
            summary = "Получить профиль текущего сотрудника",
            description = "Возвращает профиль текущего аутентифицированного сотрудника. Доступно для всех аутентифицированных пользователей."
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> getCurrentEmployeeProfile() {
        EmployeeProfileDto profile = employeeService.getCurrentEmployeeProfile();
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Обновить профиль текущего сотрудника",
            description = "Обновляет профиль текущего аутентифицированного сотрудника. Доступно для всех аутентифицированных пользователей."
    )
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> updateCurrentEmployeeProfile(
            @Valid @RequestBody EmployeeProfileDto updateDto) {
        EmployeeProfileDto updatedProfile = employeeService.updateCurrentEmployeeProfile(updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @Operation(
            summary = "Получить профиль сотрудника по ID",
            description = "Возвращает профиль сотрудника по его ID. Доступно для всех аутентифицированных пользователей."
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> getEmployeeProfileById(@PathVariable Integer id) {
        EmployeeProfileDto profile = employeeService.getEmployeeProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Обновить профиль сотрудника по ID",
            description = "Обновляет профиль сотрудника по его ID. Доступно только пользователям с ролью ADMIN."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EmployeeProfileDto> updateEmployeeProfile(
            @PathVariable Integer id, @Valid @RequestBody EmployeeProfileDto updateDto) {
        EmployeeProfileDto updatedProfile = employeeService.updateEmployeeProfile(id, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @Operation(
            summary = "Загрузить иконку профиля",
            description = "Загружает иконку профиля для текущего аутентифицированного сотрудника. Доступно для всех аутентифицированных пользователей. Поддерживаются файлы: JPEG, PNG."
    )
    @PostMapping(value = "/me/icon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> uploadProfileIcon(
            @RequestPart("icon") MultipartFile icon) {
        EmployeeProfileDto updatedProfile = employeeService.uploadProfileIcon(icon);
        return ResponseEntity.ok(updatedProfile);
    }
}
