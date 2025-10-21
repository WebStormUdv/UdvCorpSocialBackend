package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillConfirmationRequestCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillConfirmationRequestDTO;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillConfirmationRequestUpdateDTO;
import ru.backend.UdvCorpSocialBackend.service.SkillConfirmationRequestService;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@Tag(name = "Skill confirmation requests", description = "API для управления запросами на подтверждение навыков сотрудников")
@RestController
@RequestMapping("/api")
public class SkillConfirmationRequestController {

    @Autowired
    private SkillConfirmationRequestService service;

    @Operation(
            summary = "Подача запроса на подтверждение навыка",
            description = "Позволяет сотруднику подать запрос на подтверждение навыка. В запросе указываются ID навыка, желаемый уровень владения (1–5), метод подтверждения (certificate, interview, exam, diploma), документ (PDF, DOCX, PNG, JPEG до 10 МБ) и комментарий. Ограничения: не более 15 неподтвержденных навыков и 3 запросов в неделю."
    )
    @PostMapping(value = "/employees/me/skill-confirmation-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SkillConfirmationRequestDTO> createRequest(
            @Valid @ModelAttribute SkillConfirmationRequestCreateDTO dto) throws IOException {
        return ResponseEntity.status(201).body(service.createRequest(dto));
    }

    @Operation(
            summary = "Получение всех запросов на подтверждение",
            description = "Возвращает список всех запросов на подтверждение навыков, включая ID запроса, ID сотрудника, ID навыка, уровень владения и статус. Доступно только для HR и администраторов."
    )
    @GetMapping("/skill-confirmation-requests")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<List<SkillConfirmationRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @Operation(
            summary = "Получение запросов текущего сотрудника",
            description = "Возвращает список запросов на подтверждение навыков, поданных текущим сотрудником. Доступно для сотрудника и HR."
    )
    @GetMapping("/employees/me/skill-confirmation-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SkillConfirmationRequestDTO>> getMyRequests() {
        return ResponseEntity.ok(service.getRequestsByCurrentEmployee());
    }

    @Operation(
            summary = "Утверждение или отклонение запроса",
            description = "Позволяет HR или администратору утвердить (approved) или отклонить (rejected) запрос на подтверждение навыка. При утверждении навык получает статус 'confirmed' в таблице Employee_Skills. При отклонении запись удаляется."
    )
    @PutMapping("/skill-confirmation-requests/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<SkillConfirmationRequestDTO> updateRequest(@PathVariable Integer id, @Valid @RequestBody SkillConfirmationRequestUpdateDTO dto) {
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }
}