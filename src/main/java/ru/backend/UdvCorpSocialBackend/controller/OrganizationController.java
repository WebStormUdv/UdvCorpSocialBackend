package ru.backend.UdvCorpSocialBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.backend.UdvCorpSocialBackend.dto.OrganizationNodeDTO;
import ru.backend.UdvCorpSocialBackend.service.OrganizationService;

@RestController
@RequestMapping("/api/organization-structure")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<OrganizationNodeDTO> getOrganizationStructure() {
        return ResponseEntity.ok(organizationService.getOrganizationStructure());
    }
}