package ru.backend.UdvCorpSocialBackend.dto.employee;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.RoleType;
import ru.backend.UdvCorpSocialBackend.model.WorkStatus;

@Getter
@Setter
public class EmployeeResponseDto {
    private Integer id;
    private String fullName;
    private String photoUrl;
    private String position;
    private String email;
    private Boolean onlineStatus;
    private WorkStatus workStatus;
    private String workplace;
    private String telegram;
    private String mattermost;
    private RoleType role;
    private String profileLevel;
    private EmployeeProfileResponseDto profile;

    private Integer departmentId;
    private Integer subdivisionId;
    private Integer legalEntityId;
    private Integer supervisorId;
}
