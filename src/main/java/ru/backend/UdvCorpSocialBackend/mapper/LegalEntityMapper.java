package ru.backend.UdvCorpSocialBackend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityResponseDTO;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.LegalEntity;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LegalEntityMapper {

    private final EmployeeRepository employeeRepository;

    public LegalEntityResponseDTO toDTO(LegalEntity l) {
        return toDTO(l, false);
    }

    public LegalEntityResponseDTO toDTO(
            LegalEntity legalEntity,
            boolean includeEmployees
    ) {
        var dto = new LegalEntityResponseDTO();

        dto.setId(legalEntity.getId());
        dto.setName(legalEntity.getName());

        if (includeEmployees) {
            List<Employee> list = employeeRepository
                    .findByLegalEntityId(legalEntity.getId());

            dto.setEmployees(list.stream()
                    .map(this::toEmployeeDTO)
                    .collect(Collectors.toList())
            );
        }
        return dto;
    }

    public List<LegalEntityResponseDTO> toDTOList(List<LegalEntity> legalEntities) {
        return legalEntities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private LegalEntityResponseDTO.EmployeeDTO toEmployeeDTO(Employee employee) {
        LegalEntityResponseDTO.EmployeeDTO dto = new LegalEntityResponseDTO.EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setPosition(employee.getPosition());
        return dto;
    }
}
