package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Subdivision;

import java.util.List;

@Repository
public interface SubdivisionRepository extends JpaRepository<Subdivision, Integer> {
    boolean existsByDepartmentId(Integer departmentId);
    List<Subdivision> findByDepartmentId(Integer departmentId);
}
