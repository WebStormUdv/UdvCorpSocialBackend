package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubdivisionResponseDTO {
    private Integer id;
    private String name;
    private Integer departmentId;
    private String departmentName;
    private Integer headId;
    private String headFullName;
}
