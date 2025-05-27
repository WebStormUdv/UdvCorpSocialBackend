package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.backend.UdvCorpSocialBackend.dto.EmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.EmployeeSkill;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeSkillRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeSkillService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSkillService.class);

    @Autowired
    private EmployeeSkillRepository employeeSkillRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmployeeSkillDTO> getEmployeeSkills(Integer employeeId) {
        // Проверка существования сотрудника
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Получение навыков сотрудника
        List<EmployeeSkill> employeeSkills = employeeSkillRepository.findByEmployeeId(employeeId);
        logger.info("Retrieved {} skills for employee ID: {}", employeeSkills.size(), employeeId);

        // Маппинг в DTO
        return employeeSkills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EmployeeSkillDTO convertToDTO(EmployeeSkill employeeSkill) {
        EmployeeSkillDTO dto = new EmployeeSkillDTO();
        dto.setSkillId(employeeSkill.getSkill().getId());
        dto.setName(employeeSkill.getSkill().getName());
        dto.setProficiencyLevel(employeeSkill.getProficiencyLevel());
        dto.setConfirmationStatus(employeeSkill.getConfirmationStatus());
        return dto;
    }
}