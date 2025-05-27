package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.SkillCreateDto;
import ru.backend.UdvCorpSocialBackend.dto.SkillDTO;
import ru.backend.UdvCorpSocialBackend.dto.SkillGradeDescriptionDTO;
import ru.backend.UdvCorpSocialBackend.model.Skill;
import ru.backend.UdvCorpSocialBackend.model.SkillGradeDescription;
import ru.backend.UdvCorpSocialBackend.model.SkillGradeDescriptionId;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeSkillRepository;
import ru.backend.UdvCorpSocialBackend.repository.SkillGradeDescriptionRepository;
import ru.backend.UdvCorpSocialBackend.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillGradeDescriptionRepository gradeDescriptionRepository;

    @Autowired
    private EmployeeSkillRepository employeeSkillRepository;

    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public SkillDTO getSkillById(Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + id));
        return convertToDTO(skill);
    }

    @Transactional
    public SkillDTO createSkill(SkillCreateDto skillDTO) {
        if (skillRepository.existsByName(skillDTO.getName())) {
            throw new IllegalArgumentException("Skill with name " + skillDTO.getName() + " already exists");
        }

        Skill skill = new Skill();
        skill.setName(skillDTO.getName());
        skill.setType(skillDTO.getType());
        skill = skillRepository.save(skill);

        // Сохранение описаний уровней
        if (skillDTO.getGradeDescriptions() != null) {
            for (SkillGradeDescriptionDTO gradeDTO : skillDTO.getGradeDescriptions()) {
                SkillGradeDescription gradeDescription = new SkillGradeDescription();
                SkillGradeDescriptionId id = new SkillGradeDescriptionId();
                id.setSkillId(skill.getId());
                id.setGrade(gradeDTO.getGrade());
                gradeDescription.setId(id);
                gradeDescription.setSkill(skill);
                gradeDescription.setDescription(gradeDTO.getDescription());
                gradeDescriptionRepository.save(gradeDescription);
            }
        }

        return convertToDTO(skill);
    }

    @Transactional
    public SkillDTO updateSkill(Integer id, SkillCreateDto skillDTO) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + id));

        if (!skill.getName().equals(skillDTO.getName()) && skillRepository.existsByName(skillDTO.getName())) {
            throw new IllegalArgumentException("Skill with name " + skillDTO.getName() + " already exists");
        }

        skill.setName(skillDTO.getName());
        skill.setType(skillDTO.getType());
        skill = skillRepository.save(skill);

        // Обновление описаний уровней
        if (skillDTO.getGradeDescriptions() != null) {
            gradeDescriptionRepository.deleteBySkillId(id);
            for (SkillGradeDescriptionDTO gradeDTO : skillDTO.getGradeDescriptions()) {
                SkillGradeDescription gradeDescription = new SkillGradeDescription();
                SkillGradeDescriptionId gradeId = new SkillGradeDescriptionId();
                gradeId.setSkillId(skill.getId());
                gradeId.setGrade(gradeDTO.getGrade());
                gradeDescription.setId(gradeId);
                gradeDescription.setSkill(skill);
                gradeDescription.setDescription(gradeDTO.getDescription());
                gradeDescriptionRepository.save(gradeDescription);
            }
        }

        return convertToDTO(skill);
    }

    @Transactional
    public void deleteSkill(Integer id) {
        if (!skillRepository.existsById(id)) {
            throw new IllegalArgumentException("Skill not found with id: " + id);
        }
        if (employeeSkillRepository.existsBySkillId(id)) {
            throw new IllegalStateException("Cannot delete skill with id " + id + " as it is associated with employees");
        }
        gradeDescriptionRepository.deleteBySkillId(id);
        skillRepository.deleteById(id);
    }

    private SkillDTO convertToDTO(Skill skill) {
        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setType(skill.getType());
        List<SkillGradeDescriptionDTO> gradeDTOs = gradeDescriptionRepository.findBySkillId(skill.getId())
                .stream()
                .map(grade -> {
                    SkillGradeDescriptionDTO gradeDTO = new SkillGradeDescriptionDTO();
                    gradeDTO.setGrade(grade.getId().getGrade());
                    gradeDTO.setDescription(grade.getDescription());
                    return gradeDTO;
                })
                .collect(Collectors.toList());
        dto.setGradeDescriptions(gradeDTOs);
        return dto;
    }
}