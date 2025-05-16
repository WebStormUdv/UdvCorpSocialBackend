package ru.backend.UdvCorpSocialBackend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.EmployeeProfileDto;
import ru.backend.UdvCorpSocialBackend.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeProfileController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeProfileController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> getCurrentEmployeeProfile() {
        EmployeeProfileDto profile = employeeService.getCurrentEmployeeProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> updateCurrentEmployeeProfile(
            @Valid @RequestBody EmployeeProfileDto updateDto) {
        EmployeeProfileDto updatedProfile = employeeService.updateCurrentEmployeeProfile(updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeProfileDto> getEmployeeProfileById(@PathVariable Integer id) {
        EmployeeProfileDto profile = employeeService.getEmployeeProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeProfileDto> updateEmployeeProfile(
            @PathVariable Integer id, @Valid @RequestBody EmployeeProfileDto updateDto) {
        EmployeeProfileDto updatedProfile = employeeService.updateEmployeeProfile(id, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
