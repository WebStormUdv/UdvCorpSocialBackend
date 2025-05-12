package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.CommunityMembershipRequest;

@Repository
public interface CommunityMembershipRequestRepository extends JpaRepository<CommunityMembershipRequest, Integer> {
}
