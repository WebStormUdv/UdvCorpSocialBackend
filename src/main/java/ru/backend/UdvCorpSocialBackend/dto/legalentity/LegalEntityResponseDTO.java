package ru.backend.UdvCorpSocialBackend.dto.legalentity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LegalEntityResponseDTO {
    private Integer id;
    private String name;
    private List<EmployeeDTO> employees;

    @Getter
    @Setter
    public static class EmployeeDTO {
        private Integer id;
        private String fullName;
        private String position;
    }
}
