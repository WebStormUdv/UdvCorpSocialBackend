package ru.backend.UdvCorpSocialBackend.dto.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillGradeDescriptionDTO {
    @Min(1)
    @Max(5)
    private Integer grade;
    private String description;
}
