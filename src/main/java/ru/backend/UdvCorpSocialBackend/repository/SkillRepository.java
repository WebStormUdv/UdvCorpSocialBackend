package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    boolean existsByName(String name);
}
