package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.department.DepartmentCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.department.DepartmentResponseDTO;
import ru.backend.UdvCorpSocialBackend.model.Department;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.repository.DepartmentRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.SubdivisionRepository;
import ru.backend.UdvCorpSocialBackend.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SubdivisionRepository subdivisionRepository;

    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DepartmentResponseDTO getDepartmentById(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return convertToDTO(department);
    }

    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentCreateDTO departmentDTO) {
        validateDepartmentDTO(departmentDTO);
        Department department = new Department();
        department.setName(departmentDTO.getName());
        if (departmentDTO.getHeadId() != null) {
            Employee head = employeeRepository.findById(departmentDTO.getHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + departmentDTO.getHeadId()));
            department.setHead(head);
        }
        Department savedDepartment = departmentRepository.save(department);
        return convertToDTO(savedDepartment);
    }

    @Transactional
    public DepartmentResponseDTO updateDepartment(Integer id, DepartmentCreateDTO departmentDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        validateDepartmentDTO(departmentDTO);
        department.setName(departmentDTO.getName());
        if (departmentDTO.getHeadId() != null) {
            Employee head = employeeRepository.findById(departmentDTO.getHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + departmentDTO.getHeadId()));
            department.setHead(head);
        } else {
            department.setHead(null);
        }
        Department updatedDepartment = departmentRepository.save(department);
        return convertToDTO(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        if (subdivisionRepository.existsByDepartmentId(id)) {
            throw new IllegalStateException("Cannot delete department with associated subdivisions");
        }
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new IllegalStateException("Cannot delete department with associated employees");
        }
        departmentRepository.delete(department);
    }

    private DepartmentResponseDTO convertToDTO(Department department) {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        if (department.getHead() != null) {
            dto.setHeadId(department.getHead().getId());
            dto.setHeadFullName(department.getHead().getFullName());
        }
        return dto;
    }

    private void validateDepartmentDTO(DepartmentCreateDTO departmentDTO) {
        if (departmentDTO.getName() == null || departmentDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty");
        }
    }
}