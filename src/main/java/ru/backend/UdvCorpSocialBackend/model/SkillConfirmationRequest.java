package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;

import java.time.LocalDate;

@Entity
@Table(name = "Skill_Confirmation_Requests")
@Getter
@Setter
public class SkillConfirmationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "requested_proficiency_level")
    private Integer requestedProficiencyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Employee approver;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDate.now();
    }
}
