package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.GratitudeAchievement;
import ru.backend.UdvCorpSocialBackend.model.GaType;

import java.util.List;

@Repository
public interface GratitudeAchievementRepository extends JpaRepository<GratitudeAchievement, Integer> {
    List<GratitudeAchievement> findBySenderId(Integer senderId);
    List<GratitudeAchievement> findByReceiverId(Integer receiverId);
    List<GratitudeAchievement> findByType(GaType type);
}
