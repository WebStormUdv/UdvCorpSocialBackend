package ru.backend.UdvCorpSocialBackend.mapper;

import org.springframework.stereotype.Component;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.dto.employee.ExtendedEmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.model.EmployeeSkill;

@Component
public class EmployeeSkillMapper {
    public EmployeeSkillDTO toDTO(EmployeeSkill employeeSkill) {
        EmployeeSkillDTO dto = new EmployeeSkillDTO();

        dto.setSkillId(employeeSkill.getSkill().getId());
        dto.setName(employeeSkill.getSkill().getName());
        dto.setProficiencyLevel(employeeSkill.getProficiencyLevel());
        dto.setConfirmationStatus(employeeSkill.getConfirmationStatus());

        return dto;
    }

    public ExtendedEmployeeSkillDTO toExtendedDTO(EmployeeSkill employeeSkill) {
        ExtendedEmployeeSkillDTO dto = new ExtendedEmployeeSkillDTO();

        dto.setSkillId(employeeSkill.getSkill().getId());
        dto.setName(employeeSkill.getSkill().getName());
        dto.setType(employeeSkill.getSkill().getType());
        dto.setProficiencyLevel(employeeSkill.getProficiencyLevel());
        dto.setConfirmationStatus(employeeSkill.getConfirmationStatus());
        dto.setConfirmationMethod(employeeSkill.getConfirmationMethod());
        dto.setConfirmationDate(employeeSkill.getConfirmationDate());
        dto.setConfirmationDocumentUrl(employeeSkill.getConfirmationDocumentUrl());

        return dto;
    }
}
