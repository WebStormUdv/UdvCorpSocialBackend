package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.SuggestionStatus;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class SkillSuggestionUpdateDTO {
    @NotNull(message = "Status cannot be null")
    private SuggestionStatus status;

    private LocalDate approvalDate;
}
