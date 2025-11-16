package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Employee_Skills")
@Getter
@Setter
public class EmployeeSkill {
    @EmbeddedId
    private EmployeeSkillId id;

    @ManyToOne
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_status")
    private ConfirmationStatus confirmationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_method")
    private ConfirmationMethod confirmationMethod;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @Column(name = "confirmation_document_url")
    private String confirmationDocumentUrl;

}
