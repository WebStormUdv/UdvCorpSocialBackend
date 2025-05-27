package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.SkillSuggestion;

@Repository
public interface SkillSuggestionRepository extends JpaRepository<SkillSuggestion, Integer> {
    boolean existsBySkillName(String skillName);
}
