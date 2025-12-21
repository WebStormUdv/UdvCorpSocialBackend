package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.dto.employee.ExtendedEmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationMethod;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationStatus;
import ru.backend.UdvCorpSocialBackend.service.EmployeeSkillService;

import java.util.List;

@Tag(name = "Employee skill", description = "API для управления навыками сотрудников")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EmployeeSkillController {

    private final EmployeeSkillService employeeSkillService;

    @Operation(
            summary = "Получение навыков сотрудника",
            description = "Возвращает список навыков сотрудника, включая ID навыка, название, уровень владения и статус подтверждения. Доступно для сотрудника (свои навыки), HR и администраторов."
    )
    @GetMapping("/employees/{employeeId}/skills")
    @PreAuthorize("hasAnyRole('admin', 'supervisor') or (hasRole('employee') and #employeeId == authentication.principal.id)")
    public ResponseEntity<List<EmployeeSkillDTO>> getEmployeeSkills(
            @PathVariable Integer employeeId
    ) {
        try {
            List<EmployeeSkillDTO> s = employeeSkillService
                    .getEmployeeSkills(employeeId);
            return ResponseEntity.ok(s);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Получение навыков сотрудника с фильтрами",
            description = """
                    Возвращает расширенный список навыков сотрудника с возможностью фильтрации по:
                    - типу навыка (type),
                    - уровню владения (proficiencyLevel),
                    - методу подтверждения (confirmationMethod),
                    - статусу подтверждения (status).
                    Все параметры опциональны и могут комбинироваться.
                    """
    )
    @GetMapping("/employees/{employeeId}/skills/extended")
    @PreAuthorize("hasAnyRole('admin', 'supervisor') or (hasRole('employee') and #employeeId == authentication.principal.id)")
    public ResponseEntity<List<ExtendedEmployeeSkillDTO>> getEmployeeSkillsFiltered(
            @PathVariable Integer employeeId,

            @Parameter(description = "Тип навыка, например 'Technical' или 'Soft skills'")
            @RequestParam(required = false) String type,

            @Parameter(description = "Уровень владения (числовое значение)")
            @RequestParam(required = false) Integer proficiencyLevel,

            @Parameter(description = "Метод подтверждения: certificate, interview, exam, diploma")
            @RequestParam(required = false) ConfirmationMethod confirmationMethod,

            @Parameter(description = "Статус подтверждения: confirmed или unconfirmed")
            @RequestParam(required = false) ConfirmationStatus status
    ) {
        try {
            List<ExtendedEmployeeSkillDTO> s = employeeSkillService.getEmployeeSkillsFiltered(
                    employeeId, type, proficiencyLevel, confirmationMethod, status
            );
            return ResponseEntity.ok(s);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}