package ru.backend.UdvCorpSocialBackend.dto.skill;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationMethod;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class SkillConfirmationRequestCreateDTO {
    @NotNull(message = "Skill ID cannot be null")
    private Integer skillId;

    @Min(value = 1, message = "Proficiency level must be at least 1")
    @Max(value = 5, message = "Proficiency level must not exceed 5")
    private Integer requestedProficiencyLevel;

    @NotNull(message = "Confirmation method cannot be null")
    private ConfirmationMethod confirmationMethod;

    private MultipartFile document;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}