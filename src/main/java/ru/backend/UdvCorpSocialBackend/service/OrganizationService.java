package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.organization.OrganizationNodeDTO;
import ru.backend.UdvCorpSocialBackend.model.Department;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Subdivision;
import ru.backend.UdvCorpSocialBackend.repository.DepartmentRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.SubdivisionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SubdivisionRepository subdivisionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${organization.root.id}")
    private String rootId;

    @Value("${organization.root.name}")
    private String rootName;

    @Transactional(readOnly = true)
    public OrganizationNodeDTO getOrganizationStructure() {
        OrganizationNodeDTO root = new OrganizationNodeDTO();
        root.setId(rootId);
        root.setName(rootName);
        root.setChildren(new ArrayList<>());

        List<Department> departments = departmentRepository.findAllWithHeads();

        if (departments.isEmpty()) {
            return root;
        }

        List<Integer> departmentIds = departments.stream()
                .map(Department::getId)
                .toList();

        List<Subdivision> allSubdivisions = subdivisionRepository.findByDepartmentIdsWithHeads(departmentIds);

        Map<Integer, List<Subdivision>> subdivisionsByDept = allSubdivisions.stream()
                .collect(Collectors.groupingBy(s ->
                        s.getDepartment()
                                .getId())
                );

        List<Integer> subdivisionIds = allSubdivisions.stream()
                .map(Subdivision::getId)
                .toList();

        List<Employee> allEmployees = subdivisionIds.isEmpty()
                ? Collections.emptyList()
                : employeeRepository.findBySubdivisionIdsWithProfiles(subdivisionIds);

        Map<Integer, List<Employee>> employeesBySubdivision = allEmployees.stream()
                .collect(Collectors.groupingBy(e ->
                        e.getSubdivision()
                                .getId())
                );

        for (Department department : departments) {
            OrganizationNodeDTO departmentNode = new OrganizationNodeDTO();
            departmentNode.setId(String.valueOf(department.getId()));
            departmentNode.setName(department.getName());
            departmentNode.setChildren(new ArrayList<>());

            List<Subdivision> subdivisions = subdivisionsByDept
                    .getOrDefault(department.getId(), Collections.emptyList());

            for (Subdivision subdivision : subdivisions) {
                OrganizationNodeDTO subdivisionNode = new OrganizationNodeDTO();
                subdivisionNode.setId(String.valueOf(subdivision.getId()));
                subdivisionNode.setName(subdivision.getName());
                subdivisionNode.setChildren(new ArrayList<>());

                List<Employee> employees = employeesBySubdivision
                        .getOrDefault(subdivision.getId(), Collections.emptyList());

                for (Employee employee : employees) {
                    OrganizationNodeDTO employeeNode = new OrganizationNodeDTO();
                    employeeNode.setId(String.valueOf(employee.getId()));
                    employeeNode.setName(employee.getFullName());
                    employeeNode.setPosition(employee.getPosition());
                    subdivisionNode.getChildren().add(employeeNode);
                }

                departmentNode.getChildren().add(subdivisionNode);
            }

            root.getChildren().add(departmentNode);
        }

        return root;
    }
}