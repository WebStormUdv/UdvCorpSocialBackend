package ru.backend.UdvCorpSocialBackend.dto.community;

import lombok.Data;
import ru.backend.UdvCorpSocialBackend.model.CommunityRole;

@Data
public class CommunityMemberDto {
    private Integer employeeId;
    private String employeeFullName;
    private CommunityRole role;
}
