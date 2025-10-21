package ru.backend.UdvCorpSocialBackend.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String confluenceUrl;
    private Set<EmployeeDTO> employees;

    @Getter
    @Setter
    public static class EmployeeDTO {
        private Integer id;
        private String fullName;
        private String position;
    }
}
