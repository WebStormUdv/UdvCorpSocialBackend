package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.SuggestionStatus;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "Skill_Suggestions")
public class SkillSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggestion_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "suggested_by")
    private Employee suggestedBy;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "skill_type")
    private String skillType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SuggestionStatus status;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

}
