package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.LegalEntity;

import java.util.List;

@Repository
public interface LegalEntityRepository extends JpaRepository<LegalEntity, Integer> {
    List<LegalEntity> findByNameContainingIgnoreCase(String name);
}
