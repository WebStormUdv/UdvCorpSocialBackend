package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.SkillGradeDescription;
import ru.backend.UdvCorpSocialBackend.model.SkillGradeDescriptionId;

@Repository
public interface SkillGradeDescriptionRepository extends JpaRepository<SkillGradeDescription, SkillGradeDescriptionId> {
}
