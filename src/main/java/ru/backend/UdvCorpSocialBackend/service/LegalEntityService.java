package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityResponseDTO;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.LegalEntity;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.LegalEntityRepository;
import ru.backend.UdvCorpSocialBackend.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegalEntityService {

    @Autowired
    private LegalEntityRepository legalEntityRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<LegalEntityResponseDTO> getAllLegalEntities(String nameFilter) {
        List<LegalEntity> legalEntities = nameFilter != null && !nameFilter.trim().isEmpty()
                ? legalEntityRepository.findByNameContainingIgnoreCase(nameFilter)
                : legalEntityRepository.findAll();
        return legalEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LegalEntityResponseDTO getLegalEntityById(Integer id, boolean includeEmployees) {
        LegalEntity legalEntity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found with id: " + id));
        return convertToDTO(legalEntity, includeEmployees);
    }

    @Transactional
    public LegalEntityResponseDTO createLegalEntity(LegalEntityCreateDTO legalEntityDTO) {
        validateLegalEntityDTO(legalEntityDTO);
        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setName(legalEntityDTO.getName());
        LegalEntity savedLegalEntity = legalEntityRepository.save(legalEntity);
        return convertToDTO(savedLegalEntity);
    }

    @Transactional
    public LegalEntityResponseDTO updateLegalEntity(Integer id, LegalEntityCreateDTO legalEntityDTO) {
        LegalEntity legalEntity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found with id: " + id));
        validateLegalEntityDTO(legalEntityDTO);
        legalEntity.setName(legalEntityDTO.getName());
        LegalEntity updatedLegalEntity = legalEntityRepository.save(legalEntity);
        return convertToDTO(updatedLegalEntity);
    }

    @Transactional
    public void deleteLegalEntity(Integer id) {
        LegalEntity legalEntity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found with id: " + id));
        if (employeeRepository.existsByLegalEntityId(id)) {
            throw new IllegalStateException("Cannot delete legal entity with associated employees");
        }
        legalEntityRepository.delete(legalEntity);
    }

    private LegalEntityResponseDTO convertToDTO(LegalEntity legalEntity) {
        return convertToDTO(legalEntity, false);
    }

    private LegalEntityResponseDTO convertToDTO(LegalEntity legalEntity, boolean includeEmployees) {
        LegalEntityResponseDTO dto = new LegalEntityResponseDTO();
        dto.setId(legalEntity.getId());
        dto.setName(legalEntity.getName());
        if (includeEmployees) {
            List<Employee> employees = employeeRepository.findByLegalEntityId(legalEntity.getId());
            dto.setEmployees(employees.stream()
                    .map(employee -> {
                        LegalEntityResponseDTO.EmployeeDTO employeeDTO = new LegalEntityResponseDTO.EmployeeDTO();
                        employeeDTO.setId(employee.getId());
                        employeeDTO.setFullName(employee.getFullName());
                        employeeDTO.setPosition(employee.getPosition());
                        return employeeDTO;
                    })
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private void validateLegalEntityDTO(LegalEntityCreateDTO legalEntityDTO) {
        if (legalEntityDTO.getName() == null || legalEntityDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Legal entity name cannot be empty");
        }
    }
}