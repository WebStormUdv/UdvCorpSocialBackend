package ru.backend.UdvCorpSocialBackend.dto.education;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationResponseDto {

    private Integer id;

    private Integer employeeId;

    private String university;

    private String specialty;

    private String degree;

    private Integer startYear;

    private Integer endYear;
}