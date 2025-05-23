package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.SkillConfirmationRequest;

@Repository
public interface SkillConfirmationRequestRepository extends JpaRepository<SkillConfirmationRequest, Integer> {
}
