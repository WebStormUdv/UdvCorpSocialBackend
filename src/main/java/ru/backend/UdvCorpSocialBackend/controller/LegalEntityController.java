package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import ru.backend.UdvCorpSocialBackend.dto.legalentity.LegalEntityCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.legalentity.LegalEntityResponseDTO;
import ru.backend.UdvCorpSocialBackend.service.LegalEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/legal-entities")
@Tag(name = "Legal Entities", description = "API для управления юридическими лицами")
@SecurityRequirement(name = "bearerAuth")
public class LegalEntityController {

    @Autowired
    private LegalEntityService legalEntityService;

    @Operation(
            summary = "Получить все юридические лица",
            description = "Возвращает список всех юридических лиц, с возможностью фильтрации по названию. Доступно для всех аутентифицированных пользователей."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of legal entities retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LegalEntityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<LegalEntityResponseDTO>> getAllLegalEntities(
            @Parameter(description = "Optional name filter for legal entities", required = false)
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(legalEntityService.getAllLegalEntities(name));
    }

    @Operation(
            summary = "Получить юридическое лицо по ID",
            description = "Возвращает подробную информацию о юридическом лице, с возможностью включения связанных сотрудников. Доступно для всех аутентифицированных пользователей."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Legal entity details retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LegalEntityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Legal entity not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LegalEntityResponseDTO> getLegalEntityById(
            @Parameter(description = "ID of the legal entity to retrieve", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Include associated employees in response", required = false)
            @RequestParam(defaultValue = "false") boolean includeEmployees) {
        return ResponseEntity.ok(legalEntityService.getLegalEntityById(id, includeEmployees));
    }

    @Operation(
            summary = "Создать новое юридическое лицо",
            description = "Создает новое юридическое лицо. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Legal entity created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LegalEntityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid legal entity data provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN or HR role",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LegalEntityResponseDTO> createLegalEntity(
            @Parameter(description = "Legal entity data to create", required = true)
            @RequestBody LegalEntityCreateDTO legalEntityDTO) {
        return ResponseEntity.ok(legalEntityService.createLegalEntity(legalEntityDTO));
    }

    @Operation(
            summary = "Обновить юридическое лицо",
            description = "Обновляет существующее юридическое лицо по ID. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Legal entity updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LegalEntityResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid legal entity data provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN or HR role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Legal entity not found",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LegalEntityResponseDTO> updateLegalEntity(
            @Parameter(description = "ID of the legal entity to update", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Updated legal entity data", required = true)
            @RequestBody LegalEntityCreateDTO legalEntityDTO) {
        return ResponseEntity.ok(legalEntityService.updateLegalEntity(id, legalEntityDTO));
    }

    @Operation(
            summary = "Удалить юридическое лицо",
            description = "Удаляет юридическое лицо по ID, если с ним не связаны сотрудники. Доступно только пользователям с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Legal entity deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not have ADMIN or HR role",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Legal entity not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Legal entity has associated employees",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteLegalEntity(
            @Parameter(description = "ID of the legal entity to delete", required = true)
            @PathVariable Integer id) {
        legalEntityService.deleteLegalEntity(id);
        return ResponseEntity.noContent().build();
    }
}