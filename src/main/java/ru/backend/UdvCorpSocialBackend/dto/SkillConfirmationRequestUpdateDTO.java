package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class SkillConfirmationRequestUpdateDTO {
    @NotNull(message = "Status cannot be null")
    private RequestStatus status;

    private Integer approverId;
    private LocalDate approvalDate;
}