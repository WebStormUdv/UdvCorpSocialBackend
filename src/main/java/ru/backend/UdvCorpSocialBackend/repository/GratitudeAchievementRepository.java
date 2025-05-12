package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.GratitudeAchievement;

@Repository
public interface GratitudeAchievementRepository extends JpaRepository<GratitudeAchievement, Integer> {
}
