package ru.backend.UdvCorpSocialBackend.dto.employee;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.EmploymentStatus;
import ru.backend.UdvCorpSocialBackend.model.WorkStatus;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class EmployeeProfileDto {
    private Integer id;
    private String fullName;
    private String photoUrl;
    private String position;
    private String departmentName;
    private String subdivisionName;
    private String mattermost;
    private String telegram;
    private String email;
    private Boolean onlineStatus;
    private WorkStatus workStatus;
    private String supervisorName;
    private String legalEntityName;
    private String workplace;
    private String profileLevel;

    @Size(max = 50, message = "Status state must not exceed 50 characters")
    private String statusState;

    @Size(max = 140, message = "Status comment must not exceed 140 characters")
    private String statusComment;

    @Size(max = 200, message = "About me must not exceed 200 characters")
    private String aboutMe;

    private LocalDate birthday;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 500, message = "Hobbies must not exceed 500 characters")
    private String hobbies;

    private EmploymentStatus employmentStatus;
    private Set<String> projectNames;
}
