package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.backend.UdvCorpSocialBackend.dto.OrganizationNodeDTO;
import ru.backend.UdvCorpSocialBackend.service.OrganizationService;

@RestController
@RequestMapping("/api/organization-structure")
@Tag(name = "Organization Structure", description = "Получение структуры организации")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    @Operation(summary = "Получение структуры организации", description = "Возвращает структуру организации в виде дерева.")
    public ResponseEntity<OrganizationNodeDTO> getOrganizationStructure() {
        return ResponseEntity.ok(organizationService.getOrganizationStructure());
    }
}