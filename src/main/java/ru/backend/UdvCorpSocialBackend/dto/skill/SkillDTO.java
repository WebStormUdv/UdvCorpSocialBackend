package ru.backend.UdvCorpSocialBackend.dto.skill;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SkillDTO {
    private Integer id;
    private String name;
    private String type;
    private List<SkillGradeDescriptionDTO> gradeDescriptions;
}

