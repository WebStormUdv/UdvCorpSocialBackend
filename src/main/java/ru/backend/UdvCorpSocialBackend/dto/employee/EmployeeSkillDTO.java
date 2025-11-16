package ru.backend.UdvCorpSocialBackend.dto.employee;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationStatus;

@Getter
@Setter
public class EmployeeSkillDTO {
    private Integer skillId;
    private String name;
    private Integer proficiencyLevel;
    private ConfirmationStatus confirmationStatus;
}