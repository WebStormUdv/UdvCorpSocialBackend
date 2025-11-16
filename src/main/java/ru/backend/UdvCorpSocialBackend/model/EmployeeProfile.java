package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Employee_Profiles")
@Getter
@Setter
public class EmployeeProfile {
    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "status_state")
    private String statusState;

    @Column(name = "status_comment")
    private String statusComment;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "city")
    private String city;

    @Column(name = "hobbies")
    private String hobbies;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

}
