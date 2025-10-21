package ru.backend.UdvCorpSocialBackend.dto.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrganizationNodeDTO {
    private String id;
    private String name;
    private String position;
    private List<OrganizationNodeDTO> children;
}
