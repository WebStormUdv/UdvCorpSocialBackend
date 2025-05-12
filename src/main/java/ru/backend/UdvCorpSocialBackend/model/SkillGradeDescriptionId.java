package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class SkillGradeDescriptionId implements Serializable {
    @Column(name = "skill_id")
    private Integer skillId;

    @Column(name = "grade")
    private Integer grade;

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillGradeDescriptionId that = (SkillGradeDescriptionId) o;
        return skillId.equals(that.skillId) && grade.equals(that.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId, grade);
    }
}
