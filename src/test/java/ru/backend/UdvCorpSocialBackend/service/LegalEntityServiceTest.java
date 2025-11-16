package ru.backend.UdvCorpSocialBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.company.LegalEntityResponseDTO;
import ru.backend.UdvCorpSocialBackend.exception.ResourceNotFoundException;
import ru.backend.UdvCorpSocialBackend.mapper.LegalEntityMapper;
import ru.backend.UdvCorpSocialBackend.model.LegalEntity;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.LegalEntityRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LegalEntityService Tests")
class LegalEntityServiceTest {

    @Mock
    private LegalEntityRepository legalEntityRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LegalEntityMapper mapper;

    @InjectMocks
    private LegalEntityService legalEntityService;

    private LegalEntity testLegalEntity;
    private LegalEntityCreateDTO testCreateDTO;
    private LegalEntityResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        // Тестовые данные
        testLegalEntity = new LegalEntity();
        testLegalEntity.setId(1);
        testLegalEntity.setName("Test Company LLC");

        testCreateDTO = new LegalEntityCreateDTO();
        testCreateDTO.setName("Test Company LLC");

        testResponseDTO = new LegalEntityResponseDTO();
        testResponseDTO.setId(1);
        testResponseDTO.setName("Test Company LLC");
    }

    // ==================== getAllLegalEntities Tests ====================

    @Test
    @DisplayName("Should return all legal entities when no filter provided")
    void getAllLegalEntities_NoFilter_Success() {
        // Given
        List<LegalEntity> legalEntities = Arrays.asList(testLegalEntity);
        List<LegalEntityResponseDTO> expectedDTOs = Arrays.asList(testResponseDTO);

        when(legalEntityRepository.findAll()).thenReturn(legalEntities);
        when(mapper.toDTOList(legalEntities)).thenReturn(expectedDTOs);

        // When
        List<LegalEntityResponseDTO> result = legalEntityService.getAllLegalEntities(null);

        // Then
        assertEquals(1, result.size());
        verify(legalEntityRepository, times(1)).findAll();
        verify(legalEntityRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should return filtered legal entities when name filter provided")
    void getAllLegalEntities_WithFilter_Success() {
        // Given
        String nameFilter = "Test";
        List<LegalEntity> legalEntities = Arrays.asList(testLegalEntity);
        List<LegalEntityResponseDTO> expectedDTOs = Arrays.asList(testResponseDTO);

        when(legalEntityRepository.findByNameContainingIgnoreCase(nameFilter))
                .thenReturn(legalEntities);
        when(mapper.toDTOList(legalEntities)).thenReturn(expectedDTOs);

        // When
        List<LegalEntityResponseDTO> result = legalEntityService.getAllLegalEntities(nameFilter);

        // Then
        assertEquals(1, result.size());
        verify(legalEntityRepository, times(1)).findByNameContainingIgnoreCase(nameFilter);
        verify(legalEntityRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no legal entities found")
    void getAllLegalEntities_EmptyResult_Success() {
        // Given
        when(legalEntityRepository.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<LegalEntityResponseDTO> result = legalEntityService.getAllLegalEntities(null);

        // Then
        assertTrue(result.isEmpty());
        verify(legalEntityRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should ignore empty or whitespace filter")
    void getAllLegalEntities_EmptyFilter_UsesFindAll() {
        // Given
        List<LegalEntity> legalEntities = Arrays.asList(testLegalEntity);
        when(legalEntityRepository.findAll()).thenReturn(legalEntities);
        when(mapper.toDTOList(legalEntities)).thenReturn(Arrays.asList(testResponseDTO));

        // When
        legalEntityService.getAllLegalEntities("   ");

        // Then
        verify(legalEntityRepository, times(1)).findAll();
        verify(legalEntityRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    // ==================== getLegalEntityById Tests ====================

    @Test
    @DisplayName("Should return legal entity by id without employees")
    void getLegalEntityById_WithoutEmployees_Success() {
        // Given
        when(legalEntityRepository.findById(1)).thenReturn(Optional.of(testLegalEntity));
        when(mapper.toDTO(testLegalEntity, false)).thenReturn(testResponseDTO);

        // When
        LegalEntityResponseDTO result = legalEntityService.getLegalEntityById(1, false);

        // Then
        assertNotNull(result);
        assertEquals("Test Company LLC", result.getName());
        verify(legalEntityRepository, times(1)).findById(1);
        verify(mapper, times(1)).toDTO(testLegalEntity, false);
    }

    @Test
    @DisplayName("Should return legal entity by id with employees")
    void getLegalEntityById_WithEmployees_Success() {
        // Given
        when(legalEntityRepository.findById(1)).thenReturn(Optional.of(testLegalEntity));
        when(mapper.toDTO(testLegalEntity, true)).thenReturn(testResponseDTO);

        // When
        LegalEntityResponseDTO result = legalEntityService.getLegalEntityById(1, true);

        // Then
        assertNotNull(result);
        verify(mapper, times(1)).toDTO(testLegalEntity, true);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when legal entity not found by id")
    void getLegalEntityById_NotFound_ThrowsException() {
        // Given
        when(legalEntityRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> legalEntityService.getLegalEntityById(999, false));

        assertEquals("Legal entity not found with id: 999", exception.getMessage());
        verify(legalEntityRepository, times(1)).findById(999);
    }

    // ==================== createLegalEntity Tests ====================

    @Test
    @DisplayName("Should successfully create legal entity")
    void createLegalEntity_ValidData_Success() {
        // Given
        when(legalEntityRepository.save(any(LegalEntity.class))).thenReturn(testLegalEntity);
        when(mapper.toDTO(testLegalEntity)).thenReturn(testResponseDTO);

        // When
        LegalEntityResponseDTO result = legalEntityService.createLegalEntity(testCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Company LLC", result.getName());
        verify(legalEntityRepository, times(1)).save(any(LegalEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is null")
    void createLegalEntity_NullName_ThrowsException() {
        // Given
        testCreateDTO.setName(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> legalEntityService.createLegalEntity(testCreateDTO));

        assertEquals("Legal entity name cannot be empty", exception.getMessage());
        verify(legalEntityRepository, never()).save(any(LegalEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name is empty")
    void createLegalEntity_EmptyName_ThrowsException() {
        // Given
        testCreateDTO.setName("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> legalEntityService.createLegalEntity(testCreateDTO));

        assertEquals("Legal entity name cannot be empty", exception.getMessage());
        verify(legalEntityRepository, never()).save(any(LegalEntity.class));
    }

    // ==================== updateLegalEntity Tests ====================

    @Test
    @DisplayName("Should successfully update legal entity")
    void updateLegalEntity_ValidData_Success() {
        // Given
        LegalEntityCreateDTO updateDTO = new LegalEntityCreateDTO();
        updateDTO.setName("Updated Company LLC");

        LegalEntity updatedEntity = new LegalEntity();
        updatedEntity.setId(1);
        updatedEntity.setName("Updated Company LLC");

        when(legalEntityRepository.findById(1)).thenReturn(Optional.of(testLegalEntity));
        when(legalEntityRepository.save(any(LegalEntity.class))).thenReturn(updatedEntity);
        when(mapper.toDTO(updatedEntity)).thenReturn(testResponseDTO);

        // When
        LegalEntityResponseDTO result = legalEntityService.updateLegalEntity(1, updateDTO);

        // Then
        assertNotNull(result);
        verify(legalEntityRepository, times(1)).findById(1);
        verify(legalEntityRepository, times(1)).save(any(LegalEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent entity")
    void updateLegalEntity_NotFound_ThrowsException() {
        // Given
        when(legalEntityRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> legalEntityService.updateLegalEntity(999, testCreateDTO));

        assertEquals("Legal entity not found with id: 999", exception.getMessage());
        verify(legalEntityRepository, never()).save(any(LegalEntity.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating with empty name")
    void updateLegalEntity_EmptyName_ThrowsException() {
        // Given
        testCreateDTO.setName("");
        when(legalEntityRepository.findById(1)).thenReturn(Optional.of(testLegalEntity));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> legalEntityService.updateLegalEntity(1, testCreateDTO));

        assertEquals("Legal entity name cannot be empty", exception.getMessage());
        verify(legalEntityRepository, never()).save(any(LegalEntity.class));
    }

    // ==================== deleteLegalEntity Tests ====================

    @Test
    @DisplayName("Should successfully delete legal entity")
    void deleteLegalEntity_NoEmployees_Success() {
        // Given
        when(legalEntityRepository.findById(1)).thenReturn(Optional.of(testLegalEntity));
        when(employeeRepository.existsByLegalEntityId(1)).thenReturn(false);

        // When
        legalEntityService.deleteLegalEntity(1);

        // Then
        verify(legalEntityRepository, times(1)).delete(testLegalEntity);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when deleting entity with employees")
    void deleteLegalEntity_HasEmployees_ThrowsException() {
        // Given
        when(legalEntityRepository.findById(1)).thenReturn(Optional.of(testLegalEntity));
        when(employeeRepository.existsByLegalEntityId(1)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> legalEntityService.deleteLegalEntity(1));

        assertEquals("Cannot delete legal entity with associated employees", exception.getMessage());
        verify(legalEntityRepository, never()).delete(any(LegalEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent entity")
    void deleteLegalEntity_NotFound_ThrowsException() {
        // Given
        when(legalEntityRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> legalEntityService.deleteLegalEntity(999));

        assertEquals("Legal entity not found with id: 999", exception.getMessage());
        verify(legalEntityRepository, never()).delete(any(LegalEntity.class));
    }
}
