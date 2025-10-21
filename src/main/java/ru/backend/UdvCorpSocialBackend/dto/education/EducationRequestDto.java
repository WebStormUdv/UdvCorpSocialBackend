package ru.backend.UdvCorpSocialBackend.dto.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationRequestDto {

    @NotBlank(message = "University is required")
    private String university;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotNull(message = "Start year is required")
    private Integer startYear;

    @NotNull(message = "End year is required")
    private Integer endYear;
}