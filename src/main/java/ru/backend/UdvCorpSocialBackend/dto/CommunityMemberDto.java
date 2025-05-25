package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Data;
import ru.backend.UdvCorpSocialBackend.model.enums.CommunityRole;

@Data
public class CommunityMemberDto {
    private Integer employeeId;
    private String employeeFullName;
    private CommunityRole role;
}
