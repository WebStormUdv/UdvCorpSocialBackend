package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.backend.UdvCorpSocialBackend.dto.OrganizationNodeDTO;
import ru.backend.UdvCorpSocialBackend.model.Department;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Subdivision;
import ru.backend.UdvCorpSocialBackend.repository.DepartmentRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.SubdivisionRepository;

import java.util.ArrayList;
import java.util.List;

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

    public OrganizationNodeDTO getOrganizationStructure() {
        OrganizationNodeDTO root = new OrganizationNodeDTO();
        root.setId(rootId);
        root.setName(rootName);
        root.setChildren(new ArrayList<>());

        List<Department> departments = departmentRepository.findAll();
        for (Department department : departments) {
            OrganizationNodeDTO departmentNode = new OrganizationNodeDTO();
            departmentNode.setId(String.valueOf(department.getId()));
            departmentNode.setName(department.getName());
            departmentNode.setChildren(new ArrayList<>());

            List<Subdivision> subdivisions = subdivisionRepository.findByDepartmentId(department.getId());
            for (Subdivision subdivision : subdivisions) {
                OrganizationNodeDTO subdivisionNode = new OrganizationNodeDTO();
                subdivisionNode.setId(String.valueOf(subdivision.getId()));
                subdivisionNode.setName(subdivision.getName());
                subdivisionNode.setChildren(new ArrayList<>());

                List<Employee> employees = employeeRepository.findBySubdivisionId(subdivision.getId());
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