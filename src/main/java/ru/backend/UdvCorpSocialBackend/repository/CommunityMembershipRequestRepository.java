package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.CommunityMembershipRequest;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;

import java.util.List;

@Repository
public interface CommunityMembershipRequestRepository extends JpaRepository<CommunityMembershipRequest, Integer> {
    boolean existsByCommunityIdAndEmployeeIdAndStatus(Integer communityId, Integer employeeId, RequestStatus status);
    List<CommunityMembershipRequest> findByCommunityIdAndStatus(Integer communityId, RequestStatus status);
}
