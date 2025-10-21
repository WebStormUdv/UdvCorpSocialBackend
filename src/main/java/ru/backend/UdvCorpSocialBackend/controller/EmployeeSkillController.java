package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.service.EmployeeSkillService;


import java.util.List;

@Tag(name = "Employee skill", description = "API для управления навыками сотрудников")
@RestController
@RequestMapping("/api")
public class EmployeeSkillController {

    @Autowired
    private EmployeeSkillService employeeSkillService;

    @Operation(
            summary = "Получение навыков сотрудника",
            description = "Возвращает список навыков сотрудника, включая ID навыка, название, уровень владения и статус подтверждения. Доступно для сотрудника (свои навыки), HR и администраторов."
    )
    @GetMapping("/employees/{employeeId}/skills")
    @PreAuthorize("hasAnyRole('admin', 'supervisor') or (hasRole('employee') and #employeeId == authentication.principal.id)")
    public ResponseEntity<List<EmployeeSkillDTO>> getEmployeeSkills(@PathVariable Integer employeeId) {
        try {
            List<EmployeeSkillDTO> skills = employeeSkillService.getEmployeeSkills(employeeId);
            return ResponseEntity.ok(skills);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}