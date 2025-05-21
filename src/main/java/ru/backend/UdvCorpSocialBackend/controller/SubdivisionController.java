package ru.backend.UdvCorpSocialBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.SubdivisionCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.SubdivisionResponseDTO;
import ru.backend.UdvCorpSocialBackend.service.SubdivisionService;

import java.util.List;

@RestController
@RequestMapping("/api/subdivisions")
public class SubdivisionController {

    @Autowired
    private SubdivisionService subdivisionService;

    @GetMapping
    public ResponseEntity<List<SubdivisionResponseDTO>> getAllSubdivisions(@RequestParam(required = false) Integer departmentId) {
        return ResponseEntity.ok(subdivisionService.getAllSubdivisions(departmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubdivisionResponseDTO> getSubdivisionById(@PathVariable Integer id) {
        return ResponseEntity.ok(subdivisionService.getSubdivisionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SubdivisionResponseDTO> createSubdivision(@RequestBody SubdivisionCreateDTO subdivisionDTO) {
        return ResponseEntity.ok(subdivisionService.createSubdivision(subdivisionDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SubdivisionResponseDTO> updateSubdivision(@PathVariable Integer id, @RequestBody SubdivisionCreateDTO subdivisionDTO) {
        return ResponseEntity.ok(subdivisionService.updateSubdivision(id, subdivisionDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteSubdivision(@PathVariable Integer id) {
        subdivisionService.deleteSubdivision(id);
        return ResponseEntity.noContent().build();
    }
}