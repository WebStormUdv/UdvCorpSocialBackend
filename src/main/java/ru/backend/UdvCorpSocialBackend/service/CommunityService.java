package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.*;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.model.enums.CommunityRole;
import ru.backend.UdvCorpSocialBackend.model.enums.CommunityType;
import ru.backend.UdvCorpSocialBackend.model.enums.RequestStatus;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMemberRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMembershipRequestRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityMembershipRequestRepository membershipRequestRepository;
    private final EmployeeRepository employeeRepository;

    private static final int MAX_COMMUNITIES_PER_EMPLOYEE = 10;
    private static final int MAX_MEMBERSHIPS_PER_EMPLOYEE = 50;

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() -> new EntityNotFoundException("Текущий пользователь с email " + email + " не найден"));
    }

    @Transactional
    public CommunityDto createCommunity(CommunityCreateDto createDto) {
        Integer employeeId = getCurrentEmployeeId();
        Employee creator = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        // Проверка лимита сообществ
        if (communityRepository.countByCreatorId(employeeId) >= MAX_COMMUNITIES_PER_EMPLOYEE) {
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

        return mapToDto(savedCommunity);
    }

    @Transactional
    public JoinCommunityResponse joinCommunity(Integer communityId) {
        Integer employeeId = getCurrentEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        // Проверка лимита членства
        if (communityMemberRepository.countByEmployeeId(employeeId) >= MAX_MEMBERSHIPS_PER_EMPLOYEE) {
            throw new IllegalStateException("Лимит членства в сообществах (" + MAX_MEMBERSHIPS_PER_EMPLOYEE + ") достигнут");
        }

        if (communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)) {
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
            response.setMessage("Вы успешно присоединились к сообществу");
        } else {
            // Создание заявки для закрытого сообщества
            if (membershipRequestRepository.existsByCommunityIdAndEmployeeIdAndStatus(communityId, employeeId, RequestStatus.pending)) {
                throw new IllegalStateException("Заявка на вступление уже отправлена");
            }

            CommunityMembershipRequest request = new CommunityMembershipRequest();
            request.setCommunity(community);
            request.setEmployee(employee);
            request.setStatus(RequestStatus.pending);
            membershipRequestRepository.save(request);
            response.setMessage("Вы отправили заявку на вступление, ждите результат");
        }

        return response;
    }

    @Transactional
    public void processMembershipRequest(Integer requestId, boolean approve) {
        Integer employeeId = getCurrentEmployeeId();
        CommunityMembershipRequest request = membershipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Заявка с ID " + requestId + " не найдена"));

        if (request.getStatus() != RequestStatus.pending) {
            throw new IllegalStateException("Заявка уже обработана");
        }

        // Проверка, является ли текущий пользователь администратором сообщества
        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                request.getCommunity().getId(), employeeId, CommunityRole.admin)) {
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
        }

        membershipRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Page<CommunityDto> getCommunities(Pageable pageable) {
        return communityRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<MembershipRequestDto> getPendingRequests(Integer communityId) {
        Integer employeeId = getCurrentEmployeeId();
        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(communityId, employeeId, CommunityRole.admin)) {
            throw new SecurityException("Только администратор сообщества может просматривать заявки");
        }

        return membershipRequestRepository.findByCommunityIdAndStatus(communityId, RequestStatus.pending)
                .stream()
                .map(this::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CommunityDto> getMyCommunities(Pageable pageable) {
        Integer employeeId = getCurrentEmployeeId();
        return communityMemberRepository.findByEmployeeId(employeeId, pageable)
                .map(member -> mapToDto(member.getCommunity()));
    }

    @Transactional
    public LeaveCommunityResponse leaveCommunity(Integer communityId) {
        Integer employeeId = getCurrentEmployeeId();
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        // Проверка, является ли пользователь создателем сообщества
        if (community.getCreator().getId().equals(employeeId)) {
            throw new IllegalStateException("Создатель сообщества не может его покинуть");
        }

        // Проверка, состоит ли пользователь в сообществе
        CommunityMember member = communityMemberRepository.findById(new CommunityMemberId(communityId, employeeId))
                .orElseThrow(() -> new IllegalStateException("Вы не являетесь участником этого сообщества"));

        // Удаление пользователя из сообщества
        communityMemberRepository.delete(member);

        LeaveCommunityResponse response = new LeaveCommunityResponse();
        response.setMessage("Вы успешно покинули сообщество");
        return response;
    }

    @Transactional
    public CommunityDto updateCommunity(Integer communityId, CommunityCreateDto updateDto) {
        Integer employeeId = getCurrentEmployeeId();
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(communityId, employeeId, CommunityRole.admin)) {
            throw new SecurityException("Только администратор сообщества может обновлять данные");
        }

        community.setName(updateDto.getName());
        community.setDescription(updateDto.getDescription());
        community.setType(updateDto.getType());
        communityRepository.save(community);

        return mapToDto(community);
    }

    @Transactional(readOnly = true)
    public Page<CommunityMemberDto> getCommunityMembers(Integer communityId, Pageable pageable) {
        Integer employeeId = getCurrentEmployeeId();
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (community.getType() == CommunityType.closed &&
                !communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)) {
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
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено"));

        if (!communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(communityId, currentEmployeeId, CommunityRole.admin)) {
            throw new SecurityException("Только администратор может исключать участников");
        }

        if (currentEmployeeId.equals(targetEmployeeId)) {
            throw new IllegalStateException("Вы не можете исключить самого себя");
        }

        if (community.getCreator().getId().equals(targetEmployeeId)) {
            throw new IllegalStateException("Нельзя исключить создателя сообщества");
        }

        CommunityMember member = communityMemberRepository.findById(new CommunityMemberId(communityId, targetEmployeeId))
                .orElseThrow(() -> new IllegalStateException("Пользователь не является участником сообщества"));

        if (member.getRole() == CommunityRole.admin) {
            long adminCount = communityMemberRepository.findByCommunityIdAndRole(communityId, CommunityRole.admin).size();
            if (adminCount <= 1) {
                throw new IllegalStateException("Сообщество не может остаться без администраторов");
            }
        }

        communityMemberRepository.delete(member);
    }

    private CommunityDto mapToDto(Community community) {
        CommunityDto dto = new CommunityDto();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setDescription(community.getDescription());
        dto.setCreatorId(community.getCreator().getId());
        dto.setType(community.getType());
        return dto;
    }

    private MembershipRequestDto mapToRequestDto(CommunityMembershipRequest request) {
        MembershipRequestDto dto = new MembershipRequestDto();
        dto.setId(request.getId());
        dto.setCommunityId(request.getCommunity().getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setStatus(request.getStatus());
        dto.setRequestTimestamp(request.getRequestTimestamp().toString());
        if (request.getApprover() != null) {
            dto.setApproverId(request.getApprover().getId());
        }
        if (request.getApprovalTimestamp() != null) {
            dto.setApprovalTimestamp(request.getApprovalTimestamp().toString());
        }
        return dto;
    }
}