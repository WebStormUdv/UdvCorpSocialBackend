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
@Tag(name = "Projects", description = "API for managing projects and their associated employees")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(
            summary = "Get all projects",
            description = "Retrieves a list of all projects, optionally filtered by employee ID. Accessible to all authenticated users."
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
            summary = "Get project by ID",
            description = "Retrieves details of a specific project, including associated employees. Accessible to all authenticated users."
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
            summary = "Create a new project",
            description = "Creates a new project. Accessible only to users with ADMIN role."
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
            summary = "Update a project",
            description = "Updates an existing project by ID. Accessible only to users with ADMIN role."
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
            summary = "Delete a project",
            description = "Deletes a project by ID, if it has no associated employees. Accessible only to users with ADMIN role."
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
            summary = "Add employees to a project",
            description = "Adds one or more employees to a project by their IDs. Accessible only to users with ADMIN role."
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
            summary = "Remove an employee from a project",
            description = "Removes an employee from a project by their ID. Accessible only to users with ADMIN role."
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