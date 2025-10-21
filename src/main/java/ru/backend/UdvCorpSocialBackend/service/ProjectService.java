package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.project.ProjectCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.project.ProjectResponseDTO;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Project;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.ProjectRepository;
import ru.backend.UdvCorpSocialBackend.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<ProjectResponseDTO> getAllProjects(Integer employeeId) {
        List<Project> projects = employeeId != null
                ? projectRepository.findByEmployeesId(employeeId)
                : projectRepository.findAll();
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProjectResponseDTO getProjectById(Integer id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return convertToDTO(project);
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectCreateDTO projectDTO) {
        validateProjectDTO(projectDTO);
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setConfluenceUrl(projectDTO.getConfluenceUrl());
        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }

    @Transactional
    public ProjectResponseDTO updateProject(Integer id, ProjectCreateDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        validateProjectDTO(projectDTO);
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setConfluenceUrl(projectDTO.getConfluenceUrl());
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(Integer id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        if (!project.getEmployees().isEmpty()) {
            throw new IllegalStateException("Cannot delete project with associated employees");
        }
        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponseDTO addEmployeesToProject(Integer projectId, List<Integer> employeeIds) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        for (Integer employeeId : employeeIds) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
            project.getEmployees().add(employee);
            employee.getProjects().add(project);
        }
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    @Transactional
    public ProjectResponseDTO removeEmployeeFromProject(Integer projectId, Integer employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        if (!project.getEmployees().remove(employee)) {
            throw new IllegalStateException("Employee with id " + employeeId + " is not associated with project " + projectId);
        }
        employee.getProjects().remove(project);
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    private ProjectResponseDTO convertToDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setConfluenceUrl(project.getConfluenceUrl());
        dto.setEmployees(project.getEmployees().stream()
                .map(employee -> {
                    ProjectResponseDTO.EmployeeDTO employeeDTO = new ProjectResponseDTO.EmployeeDTO();
                    employeeDTO.setId(employee.getId());
                    employeeDTO.setFullName(employee.getFullName());
                    employeeDTO.setPosition(employee.getPosition());
                    return employeeDTO;
                })
                .collect(Collectors.toSet()));
        return dto;
    }

    private void validateProjectDTO(ProjectCreateDTO projectDTO) {
        if (projectDTO.getName() == null || projectDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
    }
}
