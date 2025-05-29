package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.ProjectCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.ProjectResponseDTO;
import ru.backend.UdvCorpSocialBackend.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "API для управления проектами и связанными с ними сотрудниками")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(
            summary = "Получить все проекты",
            description = "Возвращает список всех проектов, с возможностью фильтрации по ID сотрудника. Доступно для всех аутентифицированных пользователей."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of projects retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(@RequestParam(required = false) Integer employeeId) {
        return ResponseEntity.ok(projectService.getAllProjects(employeeId));
    }

    @Operation(
            summary = "Получить проект по ID",
            description = "Возвращает подробную информацию о конкретном проекте, включая связанных сотрудников. Доступно для всех аутентифицированных пользователей."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project details retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Integer id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @Operation(
            summary = "Создать новый проект",
            description = "Создает новый проект. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid project data provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectCreateDTO projectDTO) {
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    @Operation(
            summary = "Обновить проект",
            description = "Обновляет существующий проект по ID. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid project data provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Integer id, @RequestBody ProjectCreateDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }

    @Operation(
            summary = "Удалить проект",
            description = "Удаляет проект по ID, если в нем нет связанных сотрудников. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Project has associated employees",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Добавить сотрудников в проект",
            description = "Добавляет одного или нескольких сотрудников в проект по их ID. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employees added successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid employee IDs provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or employee not found",
                    content = @Content
            )
    })
    @PostMapping("/{id}/employees")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ProjectResponseDTO> addEmployeesToProject(@PathVariable Integer id, @RequestBody List<Integer> employeeIds) {
        return ResponseEntity.ok(projectService.addEmployeesToProject(id, employeeIds));
    }

    @Operation(
            summary = "Удалить сотрудника из проекта",
            description = "Удаляет сотрудника из проекта по его ID. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee removed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project or employee not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Employee is not associated with the project",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}/employees/{employeeId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ProjectResponseDTO> removeEmployeeFromProject(@PathVariable Integer id, @PathVariable Integer employeeId) {
        return ResponseEntity.ok(projectService.removeEmployeeFromProject(id, employeeId));
    }
}