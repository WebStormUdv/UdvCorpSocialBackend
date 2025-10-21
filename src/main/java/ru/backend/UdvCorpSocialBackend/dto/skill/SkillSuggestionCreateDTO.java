package ru.backend.UdvCorpSocialBackend.dto.skill;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class SkillSuggestionCreateDTO {
    @NotBlank(message = "Skill name cannot be blank")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    private String skillName;

    @Size(max = 50, message = "Skill type must not exceed 50 characters")
    private String skillType;
}
