package ru.backend.UdvCorpSocialBackend.dto.employee;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.EmploymentStatus;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeProfileResponseDto {
    private Integer employeeId;
    private String statusState;
    private String statusComment;
    private String aboutMe;
    private LocalDate birthday;
    private String city;
    private String hobbies;
    private EmploymentStatus employmentStatus;
}
