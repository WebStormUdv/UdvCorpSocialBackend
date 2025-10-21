package ru.backend.UdvCorpSocialBackend.dto.department;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentResponseDTO {
    private Integer id;
    private String name;
    private Integer headId;
    private String headFullName;
}
