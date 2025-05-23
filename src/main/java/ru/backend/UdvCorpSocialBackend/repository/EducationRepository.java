package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Education;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Integer> {
    List<Education> findByEmployeeId(Integer employeeId);
}
