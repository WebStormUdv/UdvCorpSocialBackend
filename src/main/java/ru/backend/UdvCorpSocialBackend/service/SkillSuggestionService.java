package ru.backend.UdvCorpSocialBackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillSuggestionCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillSuggestionDTO;
import ru.backend.UdvCorpSocialBackend.dto.skill.SkillSuggestionUpdateDTO;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Skill;
import ru.backend.UdvCorpSocialBackend.model.SkillSuggestion;
import ru.backend.UdvCorpSocialBackend.model.enums.SuggestionStatus;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.SkillRepository;
import ru.backend.UdvCorpSocialBackend.repository.SkillSuggestionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillSuggestionService {

    private final SkillSuggestionRepository skillSuggestionRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public SkillSuggestionDTO createSkillSuggestion(SkillSuggestionCreateDTO dto) {
        if (skillSuggestionRepository.existsBySkillName(dto.getSkillName()) || skillRepository.existsByName(dto.getSkillName())) {
            throw new IllegalArgumentException("Skill with name " + dto.getSkillName() + " already exists");
        }

        SkillSuggestion suggestion = new SkillSuggestion();
        suggestion.setSkillName(dto.getSkillName());
        suggestion.setSkillType(dto.getSkillType());
        suggestion.setStatus(SuggestionStatus.pending);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee suggestedBy = employeeRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        suggestion.setSuggestedBy(suggestedBy);

        suggestion = skillSuggestionRepository.save(suggestion);
        return convertToDTO(suggestion);
    }

    public List<SkillSuggestionDTO> getAllSkillSuggestions() {
        return skillSuggestionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillSuggestionDTO updateSkillSuggestion(Integer id, SkillSuggestionUpdateDTO dto) {
        SkillSuggestion suggestion = skillSuggestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill suggestion not found with id: " + id));

        if (dto.getStatus() != SuggestionStatus.pending) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee approvedBy = employeeRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Approver not found"));
            suggestion.setApprovedBy(approvedBy);
            suggestion.setApprovalDate(dto.getApprovalDate() != null ? dto.getApprovalDate() : LocalDate.now());
        }

        suggestion.setStatus(dto.getStatus());

        if (dto.getStatus() == SuggestionStatus.approved) {
            Skill skill = new Skill();
            skill.setName(suggestion.getSkillName());
            skill.setType(suggestion.getSkillType());
            skillRepository.save(skill);
        }

        suggestion = skillSuggestionRepository.save(suggestion);
        return convertToDTO(suggestion);
    }

    private SkillSuggestionDTO convertToDTO(SkillSuggestion suggestion) {
        SkillSuggestionDTO dto = new SkillSuggestionDTO();
        dto.setId(suggestion.getId());
        dto.setSkillName(suggestion.getSkillName());
        dto.setSkillType(suggestion.getSkillType());
        dto.setStatus(suggestion.getStatus());
        dto.setSuggestedBy(suggestion.getSuggestedBy() != null ? suggestion.getSuggestedBy().getId() : null);
        dto.setApprovedBy(suggestion.getApprovedBy() != null ? suggestion.getApprovedBy().getId() : null);
        dto.setApprovalDate(suggestion.getApprovalDate());
        return dto;
    }
}