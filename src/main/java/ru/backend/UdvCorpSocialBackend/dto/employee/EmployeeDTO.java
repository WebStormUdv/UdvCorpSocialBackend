package ru.backend.UdvCorpSocialBackend.dto.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {
    private Integer id;
    private String fullName;
    private String photoUrl;
}