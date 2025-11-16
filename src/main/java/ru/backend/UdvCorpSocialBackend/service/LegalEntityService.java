package ru.backend.UdvCorpSocialBackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityResponseDTO;
import ru.backend.UdvCorpSocialBackend.exception.ResourceNotFoundException;
import ru.backend.UdvCorpSocialBackend.mapper.LegalEntityMapper;
import ru.backend.UdvCorpSocialBackend.model.LegalEntity;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.LegalEntityRepository;

import java.util.List;

@Slf4j
@Service
public class LegalEntityService {

    private final LegalEntityRepository legalEntityRepository;
    private final EmployeeRepository employeeRepository;
    private final LegalEntityMapper mapper;

    public LegalEntityService(
            LegalEntityRepository legalEntityRepository,
            EmployeeRepository employeeRepository, LegalEntityMapper mapper
    ) {
        this.legalEntityRepository = legalEntityRepository;
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<LegalEntityResponseDTO> getAllLegalEntities(String nameFilter) {
        log.debug("Fetching all legal entities with nameFilter: {}", nameFilter);

        List<LegalEntity> legalEntities = nameFilter != null && !nameFilter.trim().isEmpty()
                ? legalEntityRepository.findByNameContainingIgnoreCase(nameFilter)
                : legalEntityRepository.findAll();

        log.info("Found {} legal entities", legalEntities.size());

        return mapper.toDTOList(legalEntities);
    }

    @Transactional(readOnly = true)
    public LegalEntityResponseDTO getLegalEntityById(Integer id, boolean includeEmployees) {
        log.debug("Fetching legal entity with id: {}, includeEmployees: {}", id, includeEmployees);

        LegalEntity legalEntity = getLegalEntity(id);

        log.info("Successfully retrieved legal entity: {}", legalEntity.getName());

        return mapper.toDTO(legalEntity, includeEmployees);
    }

    @Transactional
    public LegalEntityResponseDTO createLegalEntity(LegalEntityCreateDTO legalEntityDTO) {
        log.info("Creating new legal entity with name: {}", legalEntityDTO.getName());

        validateLegalEntityDTO(legalEntityDTO);

        LegalEntity legalEntity = new LegalEntity();
        legalEntity.setName(legalEntityDTO.getName());

        LegalEntity savedLegalEntity = legalEntityRepository.save(legalEntity);
        log.info("Successfully created legal entity with id: {}", savedLegalEntity.getId());

        return mapper.toDTO(savedLegalEntity);
    }

    @Transactional
    public LegalEntityResponseDTO updateLegalEntity(Integer id, LegalEntityCreateDTO legalEntityDTO) {
        log.info("Updating legal entity with id: {}", id);

        LegalEntity legalEntity = getLegalEntity(id);

        validateLegalEntityDTO(legalEntityDTO);

        String oldName = legalEntity.getName();
        legalEntity.setName(legalEntityDTO.getName());

        LegalEntity updatedLegalEntity = legalEntityRepository.save(legalEntity);

        log.info("Successfully updated legal entity id: {} (name: {} -> {})",
                id, oldName, updatedLegalEntity.getName());

        return mapper.toDTO(updatedLegalEntity);
    }

    @Transactional
    public void deleteLegalEntity(Integer id) {
        log.info("Attempting to delete legal entity with id: {}", id);

        LegalEntity legalEntity = getLegalEntity(id);

        if (employeeRepository.existsByLegalEntityId(id)) {
            log.warn("Cannot delete legal entity id: {} - has associated employees", id);
            throw new IllegalStateException("Cannot delete legal entity with associated employees");
        }

        legalEntityRepository.delete(legalEntity);
        log.info("Successfully deleted legal entity: {}", legalEntity.getName());
    }

    private LegalEntity getLegalEntity(Integer id) {
        return legalEntityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Legal entity not found with id: {}", id);
                    return new ResourceNotFoundException("Legal entity not found with id: " + id);
                });
    }

    private void validateLegalEntityDTO(LegalEntityCreateDTO legalEntityDTO) {
        if (legalEntityDTO.getName() == null || legalEntityDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Legal entity name cannot be empty");
        }
    }
}