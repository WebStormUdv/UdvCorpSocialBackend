package ru.backend.UdvCorpSocialBackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.backend.UdvCorpSocialBackend.dto.CreateEmployeeRequest;
import ru.backend.UdvCorpSocialBackend.dto.EmployeeResponseDto;
import ru.backend.UdvCorpSocialBackend.dto.EmployeeProfileResponseDto;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.EmployeeProfile;
import ru.backend.UdvCorpSocialBackend.model.enums.RoleType;
import ru.backend.UdvCorpSocialBackend.model.enums.WorkStatus;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Validator validator;

    public Employee createEmployee(CreateEmployeeRequest request) {
        logger.info("Creating employee with email: {}", request.getEmail());

        // Валидация входных данных
        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Проверка уникальности email
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с email " + request.getEmail() + " уже существует");
        }

        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        employee.setRole(request.getRole() != null ? request.getRole() : RoleType.employee);
        employee.setWorkStatus(request.getWorkStatus() != null ? request.getWorkStatus() : WorkStatus.in_office);
        employee.setOnlineStatus(false);
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setSubdivision(request.getSubdivision());
        employee.setLegalEntity(request.getLegalEntity());
        employee.setWorkplace(request.getWorkplace());
        employee.setTelegram(request.getTelegram());
        employee.setMattermost(request.getMattermost());
        employee.setSupervisor(request.getSupervisor());
        employee.setProfileLevel(request.getProfileLevel());

        EmployeeProfile profile = new EmployeeProfile();
        profile.setEmployee(employee);
        employee.setProfile(profile);

        Employee savedEmployee = employeeRepository.save(employee);
        logger.debug("Employee created with ID: {}", savedEmployee.getId());
        return savedEmployee;
    }

    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setPhotoUrl(employee.getPhotoUrl());
        dto.setPosition(employee.getPosition());
        dto.setEmail(employee.getEmail());
        dto.setOnlineStatus(employee.getOnlineStatus());
        dto.setWorkStatus(employee.getWorkStatus());
        dto.setWorkplace(employee.getWorkplace());
        dto.setTelegram(employee.getTelegram());
        dto.setMattermost(employee.getMattermost());
        dto.setRole(employee.getRole());
        dto.setProfileLevel(employee.getProfileLevel());

        // Устанавливаем ID для связанных сущностей
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
        }
        if (employee.getSubdivision() != null) {
            dto.setSubdivisionId(employee.getSubdivision().getId());
        }
        if (employee.getLegalEntity() != null) {
            dto.setLegalEntityId(employee.getLegalEntity().getId());
        }
        if (employee.getSupervisor() != null) {
            dto.setSupervisorId(employee.getSupervisor().getId());
        }

        // Преобразование EmployeeProfile
        EmployeeProfile profile = employee.getProfile();
        if (profile != null) {
            EmployeeProfileResponseDto profileDto = new EmployeeProfileResponseDto();
            profileDto.setEmployeeId(profile.getEmployeeId());
            profileDto.setStatusState(profile.getStatusState());
            profileDto.setStatusComment(profile.getStatusComment());
            profileDto.setAboutMe(profile.getAboutMe());
            profileDto.setBirthday(profile.getBirthday());
            profileDto.setCity(profile.getCity());
            profileDto.setHobbies(profile.getHobbies());
            profileDto.setEmploymentStatus(profile.getEmploymentStatus());
            dto.setProfile(profileDto);
        }

        return dto;
    }
}