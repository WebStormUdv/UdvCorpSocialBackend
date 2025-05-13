package ru.backend.UdvCorpSocialBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.backend.UdvCorpSocialBackend.dto.CreateEmployeeRequest;
import ru.backend.UdvCorpSocialBackend.dto.EmployeeResponseDto;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EmployeeResponseDto> createEmployee(@RequestBody CreateEmployeeRequest request) {
        Employee employee = employeeService.createEmployee(request);
        EmployeeResponseDto responseDto = employeeService.toEmployeeResponseDto(employee);
        return ResponseEntity.ok(responseDto);
    }
}
