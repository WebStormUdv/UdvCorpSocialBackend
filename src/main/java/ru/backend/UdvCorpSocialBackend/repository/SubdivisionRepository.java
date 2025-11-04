package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Subdivision;

import java.util.List;

@Repository
public interface SubdivisionRepository extends JpaRepository<Subdivision, Integer> {
    boolean existsByDepartmentId(Integer departmentId);
    List<Subdivision> findByDepartmentId(Integer departmentId);

    @Query("""
        SELECT s FROM Subdivision s
        LEFT JOIN FETCH s.head
        LEFT JOIN FETCH s.department
        WHERE s.department.id IN :departmentIds
    """)
    List<Subdivision> findByDepartmentIdsWithHeads(List<Integer> departmentIds);
}
