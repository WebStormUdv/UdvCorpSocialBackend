package ru.backend.UdvCorpSocialBackend.dto.employee;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationMethod;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationStatus;

import java.time.LocalDate;

@Getter
@Setter
public class ExtendedEmployeeSkillDTO {
    private Integer skillId;
    private String name;
    private String type;
    private Integer proficiencyLevel;
    private ConfirmationStatus confirmationStatus;
    private ConfirmationMethod confirmationMethod;
    private LocalDate confirmationDate;
    private String confirmationDocumentUrl;
}
