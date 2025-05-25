package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.CommunityMember;
import ru.backend.UdvCorpSocialBackend.model.CommunityMemberId;
import ru.backend.UdvCorpSocialBackend.model.enums.CommunityRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, CommunityMemberId> {
    boolean existsByCommunityIdAndEmployeeId(Integer communityId, Integer employeeId);
    long countByEmployeeId(Integer employeeId);
    boolean existsByCommunityIdAndEmployeeIdAndRole(Integer id, Integer employeeId, CommunityRole communityRole);
    Page<CommunityMember> findByEmployeeId(Integer employeeId, Pageable pageable);
    Page<CommunityMember> findByCommunityId(Integer communityId, Pageable pageable);
    List<CommunityMember> findByCommunityIdAndRole(Integer communityId, CommunityRole communityRole);
}
