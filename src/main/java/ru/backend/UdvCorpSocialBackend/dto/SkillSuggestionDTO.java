package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.SuggestionStatus;

import java.time.LocalDate;

@Getter
@Setter
public class SkillSuggestionDTO {
    private Integer id;
    private String skillName;
    private String skillType;
    private SuggestionStatus status;
    private Integer suggestedBy;
    private Integer approvedBy;
    private LocalDate approvalDate;
}
