package ru.backend.UdvCorpSocialBackend.dto.community;

import lombok.Data;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;

@Data
public class MembershipRequestDto {
    private Integer id;
    private Integer communityId;
    private Integer employeeId;
    private RequestStatus status;
    private String requestTimestamp;
    private Integer approverId;
    private String approvalTimestamp;
}
