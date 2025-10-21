package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.backend.UdvCorpSocialBackend.dto.employee.CreateEmployeeRequest;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeResponseDto;
import ru.backend.UdvCorpSocialBackend.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Register new employee", description = "API для управления сотрудниками")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(
            summary = "Создать нового сотрудника",
            description = "Создает нового сотрудника с указанными данными. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateEmployeeRequest.class)
                    )
            )
    })
    public ResponseEntity<EmployeeResponseDto> createEmployee(@RequestBody CreateEmployeeRequest request) {
        EmployeeResponseDto employee = employeeService.createEmployee(request);
        return ResponseEntity.ok(employee);
    }
}
