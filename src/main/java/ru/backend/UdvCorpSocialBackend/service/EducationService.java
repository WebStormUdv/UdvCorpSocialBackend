package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.EducationRequestDto;
import ru.backend.UdvCorpSocialBackend.dto.EducationResponseDto;
import ru.backend.UdvCorpSocialBackend.model.Education;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.repository.EducationRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationService {

    private static final Logger logger = LoggerFactory.getLogger(EducationService.class);

    private final EducationRepository educationRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EducationService(EducationRepository educationRepository, EmployeeRepository employeeRepository) {
        this.educationRepository = educationRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EducationResponseDto createEducation(Integer employeeId, EducationRequestDto educationRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        if (!employee.getId().equals(employeeId)) {
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
            if (!isAdmin) {
                throw new SecurityException("Only the employee or an admin can add education records");
            }
        }

        validateYears(educationRequestDto);

        Education education = new Education();
        education.setEmployee(employee);
        education.setUniversity(educationRequestDto.getUniversity());
        education.setSpecialty(educationRequestDto.getSpecialty());
        education.setDegree(educationRequestDto.getDegree());
        education.setStartYear(educationRequestDto.getStartYear());
        education.setEndYear(educationRequestDto.getEndYear());

        Education savedEducation = educationRepository.save(education);
        logger.info("Education record created with ID: {} for employee: {}", savedEducation.getId(), email);

        return mapToResponseDto(savedEducation);
    }

    @Transactional(readOnly = true)
    public List<EducationResponseDto> getEducationByEmployeeId(Integer employeeId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee currentEmployee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        if (!currentEmployee.getId().equals(employeeId) && !isAdmin) {
            throw new SecurityException("You can only view your own education records");
        }

        List<Education> educations = educationRepository.findByEmployeeId(employeeId);
        logger.info("Retrieved education records for employee ID: {}", employeeId);
        return educations.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public EducationResponseDto updateEducation(Integer educationId, EducationRequestDto educationRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education record with ID " + educationId + " not found"));

        if (!education.getEmployee().getId().equals(employee.getId())) {
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
            if (!isAdmin) {
                throw new SecurityException("Only the employee or an admin can update education records");
            }
        }

        validateYears(educationRequestDto);

        education.setUniversity(educationRequestDto.getUniversity());
        education.setSpecialty(educationRequestDto.getSpecialty());
        education.setDegree(educationRequestDto.getDegree());
        education.setStartYear(educationRequestDto.getStartYear());
        education.setEndYear(educationRequestDto.getEndYear());

        Education updatedEducation = educationRepository.save(education);
        logger.info("Education record with ID: {} updated by employee: {}", educationId, email);
        return mapToResponseDto(updatedEducation);
    }

    @Transactional
    public void deleteEducation(Integer educationId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Education record with ID " + educationId + " not found"));

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        boolean isOwner = education.getEmployee().getId().equals(employee.getId());

        if (!isOwner && !isAdmin) {
            throw new SecurityException("Only the employee or an admin can delete education records");
        }

        educationRepository.delete(education);
        logger.info("Education record with ID: {} deleted by employee: {}", educationId, email);
    }

    private void validateYears(EducationRequestDto educationRequestDto) {
        if (educationRequestDto.getStartYear() > educationRequestDto.getEndYear()) {
            throw new IllegalArgumentException("Start year must be less than or equal to end year");
        }
    }

    private EducationResponseDto mapToResponseDto(Education education) {
        EducationResponseDto dto = new EducationResponseDto();
        dto.setId(education.getId());
        dto.setEmployeeId(education.getEmployee().getId());
        dto.setUniversity(education.getUniversity());
        dto.setSpecialty(education.getSpecialty());
        dto.setDegree(education.getDegree());
        dto.setStartYear(education.getStartYear());
        dto.setEndYear(education.getEndYear());
        return dto;
    }
}