package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.SkillGradeDescription;
import ru.backend.UdvCorpSocialBackend.model.SkillGradeDescriptionId;

import java.util.List;

@Repository
public interface SkillGradeDescriptionRepository extends JpaRepository<SkillGradeDescription, SkillGradeDescriptionId> {
    List<SkillGradeDescription> findBySkillId(Integer skillId);
    void deleteBySkillId(Integer skillId);
}
