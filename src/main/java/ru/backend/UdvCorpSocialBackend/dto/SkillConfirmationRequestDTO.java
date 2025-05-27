package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.ConfirmationMethod;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;

import java.time.LocalDate;

@Getter
@Setter
public class SkillConfirmationRequestDTO {
    private Integer id;
    private Integer employeeId;
    private Integer skillId;
    private Integer requestedProficiencyLevel;
    private RequestStatus status;
    private ConfirmationMethod confirmationMethod;
    private String documentUrl;
    private String comment;
    private Integer approverId;
    private LocalDate approvalDate;
}