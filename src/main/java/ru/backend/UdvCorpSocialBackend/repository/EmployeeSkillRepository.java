package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.EmployeeSkill;
import ru.backend.UdvCorpSocialBackend.model.EmployeeSkillId;
import ru.backend.UdvCorpSocialBackend.model.enums.ConfirmationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, EmployeeSkillId> {
    boolean existsBySkillId(Integer skillId);
    long countByEmployeeIdAndConfirmationStatus(Integer employeeId, ConfirmationStatus status);
    Optional<EmployeeSkill> findByEmployeeIdAndSkillId(Integer employeeId, Integer skillId);
    List<EmployeeSkill> findByEmployeeId(Integer employeeId);
}
