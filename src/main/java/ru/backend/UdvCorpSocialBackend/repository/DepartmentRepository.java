package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Department;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    boolean existsById(Integer id);

    @Query("""
        SELECT DISTINCT d FROM Department d
        LEFT JOIN FETCH d.head
    """)
    List<Department> findAllWithHeads();
}
