package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubdivisionCreateDTO {
    private String name;
    private Integer departmentId;
    private Integer headId;
}
