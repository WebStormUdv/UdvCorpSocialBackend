package ru.backend.UdvCorpSocialBackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.dto.employee.ExtendedEmployeeSkillDTO;
import ru.backend.UdvCorpSocialBackend.mapper.EmployeeSkillMapper;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationMethod;
import ru.backend.UdvCorpSocialBackend.model.ConfirmationStatus;
import ru.backend.UdvCorpSocialBackend.model.EmployeeSkill;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeSkillRepository;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeSkillMapper mapper;

    public List<EmployeeSkillDTO> getEmployeeSkills(Integer employeeId) {
        log.debug("Fetching skills for employeeId={}", employeeId);

        List<EmployeeSkill> es = employeeSkillRepository
                .findByEmployeeId(employeeId);

        if (es.isEmpty()) {
            log.info("No skills found for employeeId={}", employeeId);
        } else {
            log.info("Retrieved {} skills for employeeId={}", es.size(), employeeId);
        }

        return es.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ExtendedEmployeeSkillDTO> getEmployeeSkillsFiltered(
            Integer employeeId,
            String type,
            Integer proficiencyLevel,
            ConfirmationMethod method,
            ConfirmationStatus status
    ) {
        log.debug(
                "Fetching extended skills for employeeId={} with filters: type={}, proficiencyLevel={}, method={}, status={}",
                employeeId, type, proficiencyLevel, method, status
        );

        List<EmployeeSkill> eSkills = employeeSkillRepository.findByEmployeeId(employeeId);

        Predicate<EmployeeSkill> predicate = byType(type)
                .and(byProficiency(proficiencyLevel))
                .and(byMethod(method))
                .and(byStatus(status));

        List<EmployeeSkill> filtered = eSkills.stream()
                .filter(predicate)
                .toList();

        log.info(
                "Retrieved {} extended skills for employeeId={} after filtering",
                filtered.size(), employeeId
        );

        return filtered.stream()
                .map(mapper::toExtendedDTO)
                .collect(Collectors.toList());
    }

    private Predicate<EmployeeSkill> byType(String type) {
        return es -> type == null
                || (es.getSkill().getType() != null
                && es.getSkill().getType().equalsIgnoreCase(type));
    }

    private Predicate<EmployeeSkill> byProficiency(Integer level) {
        return es -> level == null || level.equals(es.getProficiencyLevel());
    }

    private Predicate<EmployeeSkill> byMethod(ConfirmationMethod method) {
        return es -> method == null || method.equals(es.getConfirmationMethod());
    }

    private Predicate<EmployeeSkill> byStatus(ConfirmationStatus status) {
        return es -> status == null || status.equals(es.getConfirmationStatus());
    }

}