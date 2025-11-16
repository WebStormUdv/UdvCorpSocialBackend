package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.gratitude.GratitudeAchievementCreateDTO;
import ru.backend.UdvCorpSocialBackend.dto.gratitude.GratitudeAchievementDTO;
import ru.backend.UdvCorpSocialBackend.dto.gratitude.GratitudeAchievementUpdateDTO;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.GratitudeAchievement;
import ru.backend.UdvCorpSocialBackend.model.GaType;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.GratitudeAchievementRepository;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GratitudeAchievementService {

    private static final Logger logger = LoggerFactory.getLogger(GratitudeAchievementService.class);
    private static final long MAX_CARD_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    @Autowired
    private GratitudeAchievementRepository gratitudeAchievementRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public GratitudeAchievementDTO createGratitudeAchievement(GratitudeAchievementCreateDTO dto) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee sender = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with email: " + email));
        Employee receiver = employeeRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with ID: " + dto.getReceiverId()));

        GratitudeAchievement ga = new GratitudeAchievement();
        ga.setSender(sender);
        ga.setReceiver(receiver);
        ga.setType(dto.getType());
        ga.setContent(dto.getContent());

        // Обработка файла карточки
        if (dto.getCard() != null && !dto.getCard().isEmpty()) {
            if (dto.getCard().getSize() > MAX_CARD_SIZE_BYTES) {
                throw new IllegalStateException("Card file size exceeds 10 MB");
            }
            String cardUrl = fileStorageService.storeGratAchieveFile(dto.getCard());
            ga.setCardUrl(cardUrl);
        }

        GratitudeAchievement savedGa = gratitudeAchievementRepository.save(ga);
        logger.info("Gratitude/Achievement created with ID: {} by sender: {} for receiver: {}", savedGa.getId(), email, receiver.getId());
        return convertToDTO(savedGa);
    }

    public GratitudeAchievementDTO getGratitudeAchievement(Integer id) {
        GratitudeAchievement ga = gratitudeAchievementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gratitude/Achievement not found with ID: " + id));
        return convertToDTO(ga);
    }

    public List<GratitudeAchievementDTO> getAllGratitudeAchievements(Integer senderId, Integer receiverId, GaType type) {
        List<GratitudeAchievement> gaList;
        if (senderId != null) {
            gaList = gratitudeAchievementRepository.findBySenderId(senderId);
        } else if (receiverId != null) {
            gaList = gratitudeAchievementRepository.findByReceiverId(receiverId);
        } else if (type != null) {
            gaList = gratitudeAchievementRepository.findByType(type);
        } else {
            gaList = gratitudeAchievementRepository.findAll();
        }
        return gaList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public GratitudeAchievementDTO updateGratitudeAchievement(Integer id, GratitudeAchievementUpdateDTO dto) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        GratitudeAchievement ga = gratitudeAchievementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gratitude/Achievement not found with ID: " + id));

        Employee currentUser = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        if (!ga.getSender().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ROLE_admin")) {
            throw new SecurityException("Only sender or admin can update this gratitude/achievement");
        }

        if (dto.getContent() != null) {
            ga.setContent(dto.getContent());
        }

        if (dto.getCard() != null && !dto.getCard().isEmpty()) {
            if (dto.getCard().getSize() > MAX_CARD_SIZE_BYTES) {
                throw new IllegalStateException("Card file size exceeds 10 MB");
            }
            if (ga.getCardUrl() != null) {
                fileStorageService.deleteGratAchieveFile(ga.getCardUrl());
            }
            String cardUrl = fileStorageService.storeGratAchieveFile(dto.getCard());
            ga.setCardUrl(cardUrl);
        }

        GratitudeAchievement updatedGa = gratitudeAchievementRepository.save(ga);
        logger.info("Gratitude/Achievement updated with ID: {} by user: {}", id, email);
        return convertToDTO(updatedGa);
    }

    @Transactional
    public void deleteGratitudeAchievement(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        GratitudeAchievement ga = gratitudeAchievementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gratitude/Achievement not found with ID: " + id));

        Employee currentUser = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        if (!ga.getSender().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ROLE_admin")) {
            throw new SecurityException("Only sender or admin can delete this gratitude/achievement");
        }

        if (ga.getCardUrl() != null) {
            fileStorageService.deleteGratAchieveFile(ga.getCardUrl());
        }
        gratitudeAchievementRepository.delete(ga);
        logger.info("Gratitude/Achievement deleted with ID: {} by user: {}", id, email);
    }

    public List<GratitudeAchievementDTO> getSentGratitudeAchievements() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee sender = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with email: " + email));
        List<GratitudeAchievement> gaList = gratitudeAchievementRepository.findBySenderId(sender.getId());
        return gaList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<GratitudeAchievementDTO> getReceivedGratitudeAchievements() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee receiver = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with email: " + email));
        List<GratitudeAchievement> gaList = gratitudeAchievementRepository.findByReceiverId(receiver.getId());
        return gaList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private GratitudeAchievementDTO convertToDTO(GratitudeAchievement ga) {
        GratitudeAchievementDTO dto = new GratitudeAchievementDTO();
        dto.setId(ga.getId());
        dto.setSenderId(ga.getSender().getId());
        dto.setSenderName(ga.getSender().getFullName());
        dto.setReceiverId(ga.getReceiver().getId());
        dto.setReceiverName(ga.getReceiver().getFullName());
        dto.setType(ga.getType());
        dto.setContent(ga.getContent());
        dto.setCardUrl(ga.getCardUrl());
        dto.setTimestamp(ga.getTimestamp());
        return dto;
    }
}