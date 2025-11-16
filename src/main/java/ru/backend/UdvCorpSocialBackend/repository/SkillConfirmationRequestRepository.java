package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.SkillConfirmationRequest;
import ru.backend.UdvCorpSocialBackend.model.RequestStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SkillConfirmationRequestRepository extends JpaRepository<SkillConfirmationRequest, Integer> {
    List<SkillConfirmationRequest> findByEmployeeId(Integer employeeId);
    long countByEmployeeIdAndStatusAndCreatedDateAfter(Integer employeeId, RequestStatus status, LocalDate date);
}
