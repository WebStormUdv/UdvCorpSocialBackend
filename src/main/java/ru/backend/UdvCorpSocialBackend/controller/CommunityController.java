package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.community.*;
import ru.backend.UdvCorpSocialBackend.service.CommunityService;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@Tag(name = "Communities", description = "Контроллер для работы с сообществами")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать новое сообщество",
            description = "Создает новое сообщество (open или closed) от имени текущего пользователя."
    )
    public ResponseEntity<CommunityDto> createCommunity(@Valid @RequestBody CommunityCreateDto createDto) {
        CommunityDto communityDto = communityService.createCommunity(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(communityDto);
    }

    @PostMapping("/{communityId}/join")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Вступить в сообщество",
            description = "Позволяет текущему пользователю вступить в открытое сообщество или отправить заявку на вступление в закрытое."
    )
    public ResponseEntity<JoinCommunityResponse> joinCommunity(@PathVariable Integer communityId) {
        JoinCommunityResponse response = communityService.joinCommunity(communityId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/requests/{requestId}/process")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Обработать заявку на вступление",
            description = "Позволяет администратору сообщества одобрить или отклонить заявку на вступление."
    )
    public ResponseEntity<Void> processMembershipRequest(
            @PathVariable Integer requestId,
            @RequestParam boolean approve) {
        communityService.processMembershipRequest(requestId, approve);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить список сообществ",
            description = "Возвращает постраничный список всех сообществ."
    )
    public ResponseEntity<Page<CommunityDto>> getCommunities(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<CommunityDto> communities = communityService.getCommunities(pageable);
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/{communityId}/requests")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить заявки на вступление в сообщество",
            description = "Возвращает список ожидающих заявок на вступление в указанное сообщество (доступно только администраторам)."
    )
    public ResponseEntity<List<MembershipRequestDto>> getPendingRequests(@PathVariable Integer communityId) {
        List<MembershipRequestDto> requests = communityService.getPendingRequests(communityId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить мои сообщества",
            description = "Возвращает постраничный список сообществ, в которых текущий пользователь является участником."
    )
    public ResponseEntity<Page<CommunityDto>> getMyCommunities(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<CommunityDto> communities = communityService.getMyCommunities(pageable);
        return ResponseEntity.ok(communities);
    }

    @DeleteMapping("/{communityId}/leave")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Покинуть сообщество",
            description = "Позволяет текущему пользователю покинуть сообщество, если он не является его создателем."
    )
    public ResponseEntity<LeaveCommunityResponse> leaveCommunity(@PathVariable Integer communityId) {
        LeaveCommunityResponse response = communityService.leaveCommunity(communityId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{communityId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Обновить сообщество", description = "Позволяет администратору обновить данные сообщества.")
    public ResponseEntity<CommunityDto> updateCommunity(
            @PathVariable Integer communityId,
            @Valid @RequestBody CommunityCreateDto updateDto) {
        CommunityDto communityDto = communityService.updateCommunity(communityId, updateDto);
        return ResponseEntity.ok(communityDto);
    }

    @GetMapping("/{communityId}/members")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить список участников сообщества", description = "Возвращает постраничный список участников сообщества.")
    public ResponseEntity<Page<CommunityMemberDto>> getCommunityMembers(
            @PathVariable Integer communityId,
            @PageableDefault(size = 20, sort = "employeeId") Pageable pageable) {
        Page<CommunityMemberDto> members = communityService.getCommunityMembers(communityId, pageable);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{communityId}/members/{employeeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Исключить участника", description = "Позволяет администратору исключить участника из сообщества.")
    public ResponseEntity<Void> removeMember(@PathVariable Integer communityId, @PathVariable Integer employeeId) {
        communityService.removeMember(communityId, employeeId);
        return ResponseEntity.noContent().build();
    }
}