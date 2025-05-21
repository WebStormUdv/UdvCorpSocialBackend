package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.backend.UdvCorpSocialBackend.dto.SubdivisionCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.SubdivisionResponseDTO;
import ru.backend.UdvCorpSocialBackend.model.Department;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Subdivision;
import ru.backend.UdvCorpSocialBackend.repository.DepartmentRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.SubdivisionRepository;
import ru.backend.UdvCorpSocialBackend.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubdivisionService {

    @Autowired
    private SubdivisionRepository subdivisionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<SubdivisionResponseDTO> getAllSubdivisions(Integer departmentId) {
        List<Subdivision> subdivisions = departmentId != null
                ? subdivisionRepository.findByDepartmentId(departmentId)
                : subdivisionRepository.findAll();
        return subdivisions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SubdivisionResponseDTO getSubdivisionById(Integer id) {
        Subdivision subdivision = subdivisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subdivision not found with id: " + id));
        return convertToDTO(subdivision);
    }

    public SubdivisionResponseDTO createSubdivision(SubdivisionCreateDTO subdivisionDTO) {
        validateSubdivisionDTO(subdivisionDTO);
        Subdivision subdivision = new Subdivision();
        subdivision.setName(subdivisionDTO.getName());
        if (subdivisionDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(subdivisionDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + subdivisionDTO.getDepartmentId()));
            subdivision.setDepartment(department);
        }
        if (subdivisionDTO.getHeadId() != null) {
            Employee head = employeeRepository.findById(subdivisionDTO.getHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + subdivisionDTO.getHeadId()));
            subdivision.setHead(head);
        }
        Subdivision savedSubdivision = subdivisionRepository.save(subdivision);
        return convertToDTO(savedSubdivision);
    }

    public SubdivisionResponseDTO updateSubdivision(Integer id, SubdivisionCreateDTO subdivisionDTO) {
        Subdivision subdivision = subdivisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subdivision not found with id: " + id));
        validateSubdivisionDTO(subdivisionDTO);
        subdivision.setName(subdivisionDTO.getName());
        if (subdivisionDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(subdivisionDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + subdivisionDTO.getDepartmentId()));
            subdivision.setDepartment(department);
        } else {
            subdivision.setDepartment(null);
        }
        if (subdivisionDTO.getHeadId() != null) {
            Employee head = employeeRepository.findById(subdivisionDTO.getHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + subdivisionDTO.getHeadId()));
            subdivision.setHead(head);
        } else {
            subdivision.setHead(null);
        }
        Subdivision updatedSubdivision = subdivisionRepository.save(subdivision);
        return convertToDTO(updatedSubdivision);
    }

    public void deleteSubdivision(Integer id) {
        Subdivision subdivision = subdivisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subdivision not found with id: " + id));
        if (employeeRepository.existsBySubdivisionId(id)) {
            throw new IllegalStateException("Cannot delete subdivision with associated employees");
        }
        subdivisionRepository.delete(subdivision);
    }

    private SubdivisionResponseDTO convertToDTO(Subdivision subdivision) {
        SubdivisionResponseDTO dto = new SubdivisionResponseDTO();
        dto.setId(subdivision.getId());
        dto.setName(subdivision.getName());
        if (subdivision.getDepartment() != null) {
            dto.setDepartmentId(subdivision.getDepartment().getId());
            dto.setDepartmentName(subdivision.getDepartment().getName());
        }
        if (subdivision.getHead() != null) {
            dto.setHeadId(subdivision.getHead().getId());
            dto.setHeadFullName(subdivision.getHead().getFullName());
        }
        return dto;
    }

    private void validateSubdivisionDTO(SubdivisionCreateDTO subdivisionDTO) {
        if (subdivisionDTO.getName() == null || subdivisionDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subdivision name cannot be empty");
        }
    }
}