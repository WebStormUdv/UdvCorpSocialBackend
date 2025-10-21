package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.backend.UdvCorpSocialBackend.dto.employee.CreateEmployeeRequest;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeProfileDto;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeResponseDto;
import ru.backend.UdvCorpSocialBackend.mapper.EmployeeMapper;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.EmployeeProfile;
import ru.backend.UdvCorpSocialBackend.model.enums.RoleType;
import ru.backend.UdvCorpSocialBackend.model.enums.WorkStatus;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EmployeeMapper employeeMapper;
    private final FileStorageService fileStorageService;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder,
                           Validator validator, EmployeeMapper employeeMapper, FileStorageService fileStorageService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.employeeMapper = employeeMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public EmployeeResponseDto createEmployee(CreateEmployeeRequest request) {
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
        return employeeMapper.toResponseDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeProfileDto getCurrentEmployeeProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));
        return employeeMapper.toProfileDto(employee);
    }

    @Transactional
    public EmployeeProfileDto updateCurrentEmployeeProfile(EmployeeProfileDto updateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        EmployeeProfile profile = employee.getProfile();
        if (profile == null) {
            profile = new EmployeeProfile();
            profile.setEmployee(employee);
            profile.setEmployeeId(employee.getId());
            employee.setProfile(profile);
        }

        // Update editable fields
        profile.setAboutMe(updateDto.getAboutMe());
        profile.setCity(updateDto.getCity());
        profile.setHobbies(updateDto.getHobbies());
        profile.setBirthday(updateDto.getBirthday());
        profile.setStatusState(updateDto.getStatusState());
        profile.setStatusComment(updateDto.getStatusComment());
        profile.setEmploymentStatus(updateDto.getEmploymentStatus());

        employeeRepository.save(employee);
        return employeeMapper.toProfileDto(employee);
    }

    @Transactional
    public EmployeeProfileDto uploadProfileIcon(MultipartFile icon) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Uploading profile icon for employee with email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        try {
            // Delete old icon if exists
            if (employee.getPhotoUrl() != null) {
                fileStorageService.deleteIconFile(employee.getPhotoUrl());
                logger.debug("Deleted old icon for employee with email: {}", email);
            }

            // Upload new icon
            String photoUrl = fileStorageService.storeIconFile(icon);
            employee.setPhotoUrl(photoUrl);
            logger.debug("Icon uploaded for employee with email: {}, URL: {}", email, photoUrl);

            employeeRepository.save(employee);
            logger.info("Profile icon updated for employee with email: {}", email);
            return employeeMapper.toProfileDto(employee);
        } catch (IOException e) {
            logger.error("Failed to upload icon for employee with email: {}", email, e);
            throw new IllegalStateException("Failed to upload icon: " + e.getMessage());
        }
    }

    @Transactional
    public EmployeeProfileDto updateEmployeeProfile(Integer employeeId, EmployeeProfileDto updateDto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));

        EmployeeProfile profile = employee.getProfile();
        if (profile == null) {
            profile = new EmployeeProfile();
            profile.setEmployee(employee);
            profile.setEmployeeId(employee.getId());
            employee.setProfile(profile);
        }

        // Update editable fields
        profile.setAboutMe(updateDto.getAboutMe());
        profile.setCity(updateDto.getCity());
        profile.setHobbies(updateDto.getHobbies());
        profile.setBirthday(updateDto.getBirthday());
        profile.setStatusState(updateDto.getStatusState());
        profile.setStatusComment(updateDto.getStatusComment());
        profile.setEmploymentStatus(updateDto.getEmploymentStatus());

        employeeRepository.save(employee);
        return employeeMapper.toProfileDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeProfileDto getEmployeeProfileById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return employeeMapper.toProfileDto(employee);
    }
}