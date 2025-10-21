package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.backend.UdvCorpSocialBackend.dto.education.EducationRequestDto;
import ru.backend.UdvCorpSocialBackend.dto.education.EducationResponseDto;
import ru.backend.UdvCorpSocialBackend.service.EducationService;

import java.util.List;

@RestController
@RequestMapping("/api/employees/{employeeId}/education")
@Tag(name = "Employee Education", description = "Работа с образованием сотрудника")
public class EducationController {

    private final EducationService educationService;

    @Autowired
    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Добавить запись об образовании",
            description = "Добавляет новую запись об образовании для указанного сотрудника. Доступно только сотруднику или админу."
    )
    public ResponseEntity<EducationResponseDto> createEducation(
            @PathVariable Integer employeeId,
            @Valid @RequestBody EducationRequestDto educationRequestDto
    ) {
        EducationResponseDto createdEducation = educationService.createEducation(employeeId, educationRequestDto);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(createdEducation.getId())
                        .toUri()
        ).body(createdEducation);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить записи об образовании сотрудника",
            description = "Возвращает список записей об образовании указанного сотрудника. Доступно только сотруднику или админу."
    )
    public ResponseEntity<List<EducationResponseDto>> getEducationByEmployeeId(@PathVariable Integer employeeId) {
        List<EducationResponseDto> educations = educationService.getEducationByEmployeeId(employeeId);
        return ResponseEntity.ok(educations);
    }

    @PutMapping("/{educationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Обновить запись об образовании",
            description = "Обновляет существующую запись об образовании. Доступно только сотруднику или админу."
    )
    public ResponseEntity<EducationResponseDto> updateEducation(
            @PathVariable Integer employeeId,
            @PathVariable Integer educationId,
            @Valid @RequestBody EducationRequestDto educationRequestDto
    ) {
        EducationResponseDto updatedEducation = educationService.updateEducation(educationId, educationRequestDto);
        return ResponseEntity.ok(updatedEducation);
    }

    @DeleteMapping("/{educationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Удалить запись об образовании",
            description = "Удаляет запись об образовании. Доступно только сотруднику или админу."
    )
    public ResponseEntity<Void> deleteEducation(
            @PathVariable Integer employeeId,
            @PathVariable Integer educationId
    ) {
        educationService.deleteEducation(educationId);
        return ResponseEntity.noContent().build();
    }
}