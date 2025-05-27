package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.SkillConfirmationRequestCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.SkillConfirmationRequestDTO;
import ru.backend.UdvCorpSocialBackend.dto.SkillConfirmationRequestUpdateDTO;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.model.enums.ConfirmationStatus;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;
import ru.backend.UdvCorpSocialBackend.repository.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillConfirmationRequestService {

    @Autowired
    private SkillConfirmationRequestRepository requestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private EmployeeSkillRepository employeeSkillRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public SkillConfirmationRequestDTO createRequest(SkillConfirmationRequestCreateDTO dto) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Проверка ограничений
        long unconfirmedSkills = employeeSkillRepository.countByEmployeeIdAndConfirmationStatus(employee.getId(), ConfirmationStatus.unconfirmed);
        if (unconfirmedSkills >= 15) {
            throw new IllegalStateException("Maximum 15 unconfirmed skills allowed");
        }

        long weeklyRequests = requestRepository.countByEmployeeIdAndStatusAndCreatedDateAfter(
                employee.getId(), RequestStatus.pending, LocalDate.now().minusDays(7));
        if (weeklyRequests >= 3) {
            throw new IllegalStateException("Maximum 3 pending requests per week allowed");
        }

        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + dto.getSkillId()));

        // Загрузка документа в MinIO
        String documentUrl = fileStorageService.storeSkillDocFile(dto.getDocument());

        // Создание запроса
        SkillConfirmationRequest request = new SkillConfirmationRequest();
        request.setEmployee(employee);
        request.setSkill(skill);
        request.setRequestedProficiencyLevel(dto.getRequestedProficiencyLevel());
        request.setStatus(RequestStatus.pending);
        request = requestRepository.save(request);

        // Создание или обновление записи в Employee_Skills
        EmployeeSkillId skillId = new EmployeeSkillId();
        skillId.setEmployeeId(employee.getId());
        skillId.setSkillId(skill.getId());

        EmployeeSkill employeeSkill = employeeSkillRepository.findByEmployeeIdAndSkillId(employee.getId(), skill.getId())
                .orElse(new EmployeeSkill());
        employeeSkill.setId(skillId);
        employeeSkill.setEmployee(employee);
        employeeSkill.setSkill(skill);
        employeeSkill.setProficiencyLevel(dto.getRequestedProficiencyLevel());
        employeeSkill.setConfirmationStatus(ConfirmationStatus.unconfirmed);
        employeeSkill.setConfirmationMethod(dto.getConfirmationMethod());
        employeeSkill.setConfirmationDocumentUrl(documentUrl);
        employeeSkillRepository.save(employeeSkill);

        return convertToDTO(request);
    }

    public List<SkillConfirmationRequestDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SkillConfirmationRequestDTO> getRequestsByCurrentEmployee() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return requestRepository.findByEmployeeId(employee.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillConfirmationRequestDTO updateRequest(Integer id, SkillConfirmationRequestUpdateDTO dto) {
        SkillConfirmationRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with id: " + id));

        if (dto.getStatus() != RequestStatus.pending) {
            Employee approver = employeeRepository.findById(dto.getApproverId())
                    .orElseThrow(() -> new IllegalArgumentException("Approver not found with id: " + dto.getApproverId()));
            request.setApprover(approver);
            request.setApprovalDate(dto.getApprovalDate() != null ? dto.getApprovalDate() : LocalDate.now());
        }

        request.setStatus(dto.getStatus());
        request = requestRepository.save(request);

        // Обновление Employee_Skills
        EmployeeSkill employeeSkill = employeeSkillRepository.findByEmployeeIdAndSkillId(
                        request.getEmployee().getId(), request.getSkill().getId())
                .orElseThrow(() -> new IllegalStateException("Employee skill not found"));

        if (dto.getStatus() == RequestStatus.approved) {
            employeeSkill.setConfirmationStatus(ConfirmationStatus.confirmed);
            employeeSkill.setConfirmationDate(LocalDate.now());
        } else if (dto.getStatus() == RequestStatus.rejected) {
            fileStorageService.deleteSkillDocFile(employeeSkill.getConfirmationDocumentUrl());
            employeeSkillRepository.delete(employeeSkill);
        }

        if (dto.getStatus() == RequestStatus.approved) {
            employeeSkillRepository.save(employeeSkill);
        }

        return convertToDTO(request);
    }

    private SkillConfirmationRequestDTO convertToDTO(SkillConfirmationRequest request) {
        SkillConfirmationRequestDTO dto = new SkillConfirmationRequestDTO();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setSkillId(request.getSkill().getId());
        dto.setRequestedProficiencyLevel(request.getRequestedProficiencyLevel());
        dto.setStatus(request.getStatus());
        dto.setConfirmationMethod(employeeSkillRepository.findByEmployeeIdAndSkillId(
                        request.getEmployee().getId(), request.getSkill().getId())
                .map(EmployeeSkill::getConfirmationMethod)
                .orElse(null));
        dto.setDocumentUrl(employeeSkillRepository.findByEmployeeIdAndSkillId(
                        request.getEmployee().getId(), request.getSkill().getId())
                .map(EmployeeSkill::getConfirmationDocumentUrl)
                .orElse(null));
        dto.setApproverId(request.getApprover() != null ? request.getApprover().getId() : null);
        dto.setApprovalDate(request.getApprovalDate());
        return dto;
    }
}