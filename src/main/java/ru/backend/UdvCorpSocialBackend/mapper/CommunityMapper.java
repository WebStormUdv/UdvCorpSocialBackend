package ru.backend.UdvCorpSocialBackend.mapper;

import org.springframework.stereotype.Component;
import ru.backend.UdvCorpSocialBackend.dto.community.CommunityDto;
import ru.backend.UdvCorpSocialBackend.dto.community.MembershipRequestDto;
import ru.backend.UdvCorpSocialBackend.model.Community;
import ru.backend.UdvCorpSocialBackend.model.CommunityMembershipRequest;

@Component
public class CommunityMapper {

    public CommunityDto toCommunityDto(Community community) {
        CommunityDto dto = new CommunityDto();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setDescription(community.getDescription());
        dto.setPhotoUrl(community.getPhotoUrl());
        dto.setCreatorId(community.getCreator().getId());
        dto.setType(community.getType());
        return dto;
    }

    public MembershipRequestDto toMembershipRequestDto(CommunityMembershipRequest request) {
        MembershipRequestDto dto = new MembershipRequestDto();
        dto.setId(request.getId());
        dto.setCommunityId(request.getCommunity().getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setStatus(request.getStatus());
        dto.setRequestTimestamp(request.getRequestTimestamp().toString());
        if (request.getApprover() != null) {
            dto.setApproverId(request.getApprover().getId());
        }
        if (request.getApprovalTimestamp() != null) {
            dto.setApprovalTimestamp(request.getApprovalTimestamp().toString());
        }
        return dto;
    }
}
