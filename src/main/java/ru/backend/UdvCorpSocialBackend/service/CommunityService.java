package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.backend.UdvCorpSocialBackend.dto.community.*;
import ru.backend.UdvCorpSocialBackend.mapper.CommunityMapper;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMemberRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMembershipRequestRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityMembershipRequestRepository membershipRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CommunityMapper mapper;
    private final FileStorageService fileStorageService;

    private static final int MAX_COMMUNITIES_PER_EMPLOYEE = 10;
    private static final int MAX_MEMBERSHIPS_PER_EMPLOYEE = 50;

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Текущий пользователь с email " + email + " не найден")
                );
    }

    @Transactional
    public CommunityDto createCommunity(CommunityCreateDto createDto) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} attempts to create community '{}'", employeeId, createDto.getName());

        Employee creator = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found, ID={}", employeeId);
                    return new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден");
                });

        // Проверка лимита сообществ
        if (communityRepository.countByCreatorId(employeeId) >= MAX_COMMUNITIES_PER_EMPLOYEE) {
            log.warn("Employee {} reached max communities limit", employeeId);
            throw new IllegalStateException("Лимит создания сообществ (" + MAX_COMMUNITIES_PER_EMPLOYEE + ") достигнут");
        }

        Community community = new Community();
        community.setName(createDto.getName());
        community.setDescription(createDto.getDescription());
        community.setCreator(creator);
        community.setType(createDto.getType());

        Community savedCommunity = communityRepository.save(community);

        // Добавление создателя как администратора
        CommunityMember member = new CommunityMember();
        CommunityMemberId memberId = new CommunityMemberId();
        memberId.setCommunityId(savedCommunity.getId());
        memberId.setEmployeeId(employeeId);
        member.setId(memberId);
        member.setCommunity(savedCommunity);
        member.setEmployee(creator);
        member.setRole(CommunityRole.admin);
        communityMemberRepository.save(member);

        log.info("Community '{}' (id={}) created by employee {}", savedCommunity.getName(), savedCommunity.getId(), employeeId);
        return mapper.toCommunityDto(savedCommunity);
    }

    @Transactional
    public JoinCommunityResponse joinCommunity(Integer communityId) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} attempts to join community {}", employeeId, communityId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found, ID={}", employeeId);
                    return new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден");
                });

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> {
                    log.error("Community not found, id={}", communityId);
                    return new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено");
                });

        // Проверка лимита членства
        if (communityMemberRepository.countByEmployeeId(employeeId) >= MAX_MEMBERSHIPS_PER_EMPLOYEE) {
            log.warn("Employee {} reached membership limit", employeeId);
            throw new IllegalStateException("Лимит членства в сообществах (" + MAX_MEMBERSHIPS_PER_EMPLOYEE + ") достигнут");
        }

        if (communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)) {
            log.warn("Employee {} already member of community {}", employeeId, communityId);
            throw new IllegalStateException("Вы уже являетесь участником этого сообщества");
        }

        JoinCommunityResponse response = new JoinCommunityResponse();

        if (community.getType() == CommunityType.open) {
            // Прямое вступление в открытое сообщество
            CommunityMember member = new CommunityMember();
            CommunityMemberId memberId = new CommunityMemberId();

            memberId.setCommunityId(communityId);
            memberId.setEmployeeId(employeeId);

            member.setId(memberId);
            member.setCommunity(community);
            member.setEmployee(employee);
            member.setRole(CommunityRole.member);

            communityMemberRepository.save(member);
            log.info("Employee {} joined open community {}", employeeId, communityId);

            response.setMessage("Вы успешно присоединились к сообществу");
        } else {
            // Создание заявки для закрытого сообщества
            if (membershipRequestRepository.existsByCommunityIdAndEmployeeIdAndStatus(communityId, employeeId, RequestStatus.pending)) {
                log.warn("Employee {} has already sent join request to community {}", employeeId, communityId);
                throw new IllegalStateException("Заявка на вступление уже отправлена");
            }

            CommunityMembershipRequest request = new CommunityMembershipRequest();

            request.setCommunity(community);
            request.setEmployee(employee);
            request.setStatus(RequestStatus.pending);
            membershipRequestRepository.save(request);

            log.info("Employee {} sent join request to closed community {}", employeeId, communityId);
            response.setMessage("Вы отправили заявку на вступление, ждите результат");
        }
        return response;
    }

    @Transactional
    public void processMembershipRequest(Integer requestId, boolean approve) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} processes membership request {} (approve={})", employeeId, requestId, approve);

        CommunityMembershipRequest request = membershipRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Membership request not found, id={}", requestId);
                    return new EntityNotFoundException("Заявка с ID " + requestId + " не найдена");
                });

        if (request.getStatus() != RequestStatus.pending) {
            log.warn("Membership request {} already processed", requestId);
            throw new IllegalStateException("Заявка уже обработана");
        }

        // Проверка, является ли текущий пользователь администратором сообщества
        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                request.getCommunity().getId(), employeeId, CommunityRole.admin)) {
            log.warn("Employee {} is not admin in community {} - access denied for processing", employeeId, request.getCommunity().getId());
            throw new SecurityException("Только администратор сообщества может обрабатывать заявки");
        }

        Employee approver = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        request.setApprover(approver);
        request.setApprovalTimestamp(LocalDateTime.now());
        request.setStatus(approve ? RequestStatus.approved : RequestStatus.rejected);

        if (approve) {
            // Добавление сотрудника в сообщество
            CommunityMember member = new CommunityMember();
            CommunityMemberId memberId = new CommunityMemberId();

            memberId.setCommunityId(request.getCommunity().getId());
            memberId.setEmployeeId(request.getEmployee().getId());

            member.setId(memberId);
            member.setCommunity(request.getCommunity());
            member.setEmployee(request.getEmployee());
            member.setRole(CommunityRole.member);

            communityMemberRepository.save(member);
            log.info("Membership request approved: employee {} added to community {}", request.getEmployee().getId(), request.getCommunity().getId());
        } else {
            log.info("Membership request {} was rejected by admin {}", requestId, employeeId);
        }
        membershipRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Page<CommunityDto> getCommunities(Pageable pageable) {
        log.debug("Fetching all communities (pageable={})", pageable);
        return communityRepository.findAll(pageable).map(mapper::toCommunityDto);
    }

    @Transactional(readOnly = true)
    public List<MembershipRequestDto> getPendingRequests(Integer communityId) {
        Integer employeeId = getCurrentEmployeeId();
        log.debug("Employee {} requests pending membership applications in community {}", employeeId, communityId);

        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                communityId, employeeId, CommunityRole.admin
        )) {
            log.warn("User {} tried to access membership requests for community {} but is not admin", employeeId, communityId);
            throw new SecurityException("Только администратор сообщества может просматривать заявки");
        }

        return membershipRequestRepository.findByCommunityIdAndStatus(communityId, RequestStatus.pending)
                .stream()
                .map(mapper::toMembershipRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CommunityDto> getMyCommunities(Pageable pageable) {
        Integer employeeId = getCurrentEmployeeId();
        log.debug("Fetching communities for employee {}", employeeId);

        return communityMemberRepository.findByEmployeeId(employeeId, pageable)
                .map(member ->
                        mapper.toCommunityDto(member.getCommunity()));
    }

    @Transactional
    public LeaveCommunityResponse leaveCommunity(Integer communityId) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} attempts to leave community {}", employeeId, communityId);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        // Проверка, является ли пользователь создателем сообщества
        if (community.getCreator().getId().equals(employeeId)) {
            log.warn("Creator (employee {}) can't leave own community {}", employeeId, communityId);
            throw new IllegalStateException("Создатель сообщества не может его покинуть");
        }

        // Проверка, состоит ли пользователь в сообществе
        CommunityMember member = communityMemberRepository.findById(new CommunityMemberId(communityId, employeeId))
                .orElseThrow(() -> new IllegalStateException("Вы не являетесь участником этого сообщества"));

        // Удаление пользователя из сообщества
        communityMemberRepository.delete(member);

        log.info("Employee {} successfully left community {}", employeeId, communityId);

        LeaveCommunityResponse response = new LeaveCommunityResponse();
        response.setMessage("Вы успешно покинули сообщество");
        return response;
    }

    @Transactional
    public CommunityDto updateCommunity(Integer communityId, CommunityCreateDto updateDto) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} updating community {} with new data", employeeId, communityId);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                communityId, employeeId, CommunityRole.admin
        )) {
            log.warn("Employee {} attempted forbidden update on community {}", employeeId, communityId);
            throw new SecurityException("Только администратор сообщества может обновлять данные");
        }

        community.setName(updateDto.getName());
        community.setDescription(updateDto.getDescription());
        community.setType(updateDto.getType());
        communityRepository.save(community);

        log.info("Employee {} updated community {}", employeeId, communityId);

        return mapper.toCommunityDto(community);
    }

    @Transactional(readOnly = true)
    public Page<CommunityMemberDto> getCommunityMembers(Integer communityId, Pageable pageable) {
        Integer employeeId = getCurrentEmployeeId();
        log.debug("Employee {} fetching members for community {}", employeeId, communityId);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (community.getType() == CommunityType.closed &&
                !communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)
        ) {
            log.warn("Employee {} tried to fetch members of closed community {} without membership", employeeId, communityId);
            throw new SecurityException("Только участники могут просматривать список членов закрытого сообщества");
        }

        return communityMemberRepository.findByCommunityId(communityId, pageable)
                .map(member -> {
                    CommunityMemberDto dto = new CommunityMemberDto();
                    dto.setEmployeeId(member.getEmployee().getId());
                    dto.setEmployeeFullName(member.getEmployee().getFullName());
                    dto.setRole(member.getRole());
                    return dto;
                });
    }

    @Transactional
    public void removeMember(Integer communityId, Integer targetEmployeeId) {
        Integer currentEmployeeId = getCurrentEmployeeId();
        log.info("Employee {} attempts to remove employee {} from community {}", currentEmployeeId, targetEmployeeId, communityId);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                communityId, currentEmployeeId, CommunityRole.admin
        )) {
            log.warn("Employee {} is not admin (can't remove others) in community {}", currentEmployeeId, communityId);
            throw new SecurityException("Только администратор может исключать участников");
        }

        if (currentEmployeeId.equals(targetEmployeeId)) {
            log.warn("Employee {} tried to remove themselves from community {}", currentEmployeeId, communityId);
            throw new IllegalStateException("Вы не можете исключить самого себя");
        }

        if (community.getCreator().getId().equals(targetEmployeeId)) {
            log.warn("Attempt to remove community creator (id={}) from community {}", targetEmployeeId, communityId);
            throw new IllegalStateException("Нельзя исключить создателя сообщества");
        }

        CommunityMember member = communityMemberRepository
                .findById(new CommunityMemberId(communityId, targetEmployeeId))
                .orElseThrow(() -> new IllegalStateException("Пользователь не является участником сообщества"));

        if (member.getRole() == CommunityRole.admin) {
            long adminCount = communityMemberRepository
                    .findByCommunityIdAndRole(communityId, CommunityRole.admin)
                    .size();
            if (adminCount <= 1) {
                log.warn("Attempt to remove the last admin from community {}", communityId);
                throw new IllegalStateException("Сообщество не может остаться без администраторов");
            }
        }

        communityMemberRepository.delete(member);
        log.info("Employee {} removed employee {} from community {}", currentEmployeeId, targetEmployeeId, communityId);
    }

    @Transactional
    public CommunityDto updateCommunityIcon(Integer communityId, MultipartFile iconFile) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} is updating icon for community {}", employeeId, communityId);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                communityId, employeeId, CommunityRole.admin
        )) {
            log.warn("Employee {} attempted to update icon for community {} without admin rights", employeeId, communityId);
            throw new SecurityException("Только администратор сообщества может обновлять иконку");
        }

        String oldIconUrl = community.getPhotoUrl();
        String newIconUrl;
        try {
            newIconUrl = fileStorageService.storeCommunityIcon(iconFile);
        } catch (IOException ex) {
            log.error("Failed to store new community icon for community {}: {}", communityId, ex.getMessage());
            throw new RuntimeException("Ошибка загрузки файла иконки сообщества", ex);
        }

        community.setPhotoUrl(newIconUrl);
        communityRepository.save(community);

        if (oldIconUrl != null && !oldIconUrl.isEmpty()) {
            fileStorageService.deleteCommunityIcon(oldIconUrl);
        }

        log.info("Community {} icon updated by admin {}", communityId, employeeId);
        return mapper.toCommunityDto(community);
    }
}