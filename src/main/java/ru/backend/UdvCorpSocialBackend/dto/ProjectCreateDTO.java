package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateDTO {
    private String name;
    private String description;
    private String confluenceUrl;
}
