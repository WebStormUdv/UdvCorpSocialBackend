package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Employee;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmail(String email);
    boolean existsByDepartmentId(Integer departmentId);
    boolean existsBySubdivisionId(Integer subdivisionId);
    List<Employee> findBySubdivisionId(Integer subdivisionId);
    List<Employee> findByLegalEntityId(Integer legalEntityId);
    boolean existsByLegalEntityId(Integer legalEntityId);
}
