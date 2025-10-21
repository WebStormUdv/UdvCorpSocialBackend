package ru.backend.UdvCorpSocialBackend.dto.subdivision;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubdivisionCreateDTO {
    private String name;
    private Integer departmentId;
    private Integer headId;
}
