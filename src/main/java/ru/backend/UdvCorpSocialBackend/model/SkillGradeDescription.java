package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Skill_Grade_Descriptions")
public class SkillGradeDescription {
    @EmbeddedId
    private SkillGradeDescriptionId id;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "description", nullable = false)
    private String description;

}
