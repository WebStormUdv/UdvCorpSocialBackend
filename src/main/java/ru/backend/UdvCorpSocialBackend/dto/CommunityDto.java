package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Data;
import ru.backend.UdvCorpSocialBackend.model.enums.CommunityType;

@Data
public class CommunityDto {
    private Integer id;
    private String name;
    private String description;
    private Integer creatorId;
    private CommunityType type;
}
