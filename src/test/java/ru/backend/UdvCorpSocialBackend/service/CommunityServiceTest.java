package ru.backend.UdvCorpSocialBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import ru.backend.UdvCorpSocialBackend.dto.community.CommunityCreateDto;
import ru.backend.UdvCorpSocialBackend.dto.community.CommunityDto;
import ru.backend.UdvCorpSocialBackend.dto.community.JoinCommunityResponse;
import ru.backend.UdvCorpSocialBackend.dto.community.LeaveCommunityResponse;
import ru.backend.UdvCorpSocialBackend.mapper.CommunityMapper;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMemberRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMembershipRequestRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CommunityServiceTest {

    @InjectMocks
    private CommunityService service;

    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private CommunityMemberRepository communityMemberRepository;
    @Mock
    private CommunityMembershipRequestRepository membershipRequestRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CommunityMapper mapper;
    @Mock
    private FileStorageService fileStorageService;

    @BeforeEach
    void beforeEach() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "testuser@mail.ru",
                null,
                java.util.Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Employee testEmployee = new Employee();
        testEmployee.setId(1);
        lenient().when(employeeRepository.findByEmail("testuser@mail.ru"))
                .thenReturn(Optional.of(testEmployee));
    }

    @Test
    void createCommunity_succeeds() {
        CommunityCreateDto createDto = new CommunityCreateDto();
        createDto.setName("Test Community");
        createDto.setDescription("desc");
        createDto.setType(CommunityType.open);

        Integer employeeId = 1;
        Employee creator = new Employee();
        creator.setId(employeeId);
        Community community = new Community();
        community.setId(10);
        community.setCreator(creator);
        CommunityDto communityDto = new CommunityDto();

        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(creator));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(creator));
        when(communityRepository.countByCreatorId(employeeId)).thenReturn(0L);
        when(communityRepository.save(any())).thenReturn(community);
        when(mapper.toCommunityDto(any())).thenReturn(communityDto);

        CommunityDto result = service.createCommunity(createDto);

        assertThat(result).isSameAs(communityDto);
        verify(communityRepository).save(any());
        verify(communityMemberRepository).save(any());
    }

    @Test
    void joinCommunity_success_open() {
        Integer employeeId = 1, communityId = 2;
        Employee employee = new Employee();
        employee.setId(employeeId);
        Community community = new Community();
        community.setId(communityId);
        community.setType(CommunityType.open);

        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityMemberRepository.countByEmployeeId(employeeId)).thenReturn(0L);
        when(communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)).thenReturn(false);

        JoinCommunityResponse response = service.joinCommunity(communityId);

        assertThat(response.getMessage()).contains("успешно присоединились");
        verify(communityMemberRepository).save(any());
    }

    @Test
    void joinCommunity_alreadyMember_throws() {
        Integer employeeId = 1, communityId = 2;
        Employee employee = new Employee();
        employee.setId(employeeId);
        Community community = new Community();
        community.setId(communityId);
        community.setType(CommunityType.open);
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityMemberRepository.countByEmployeeId(employeeId)).thenReturn(0L);
        when(communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)).thenReturn(true);

        assertThatThrownBy(() -> service.joinCommunity(communityId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateCommunityIcon_success() throws IOException {
        Integer communityId = 10, employeeId = 1;
        Community community = new Community();
        community.setId(communityId);
        MultipartFile iconFile = mock(MultipartFile.class);
        CommunityDto dto = new CommunityDto();

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(communityId, employeeId, CommunityRole.admin)).thenReturn(true);
        when(fileStorageService.storeCommunityIcon(iconFile)).thenReturn("someUrl");
        when(mapper.toCommunityDto(any())).thenReturn(dto);

        CommunityDto result = service.updateCommunityIcon(communityId, iconFile);

        assertThat(result).isEqualTo(dto);
        verify(communityRepository).save(any());
        verify(fileStorageService).storeCommunityIcon(iconFile);
    }

    @Test
    void updateCommunityIcon_ioexception() throws IOException {
        Integer communityId = 10, employeeId = 1;
        Community community = new Community();
        community.setId(communityId);
        MultipartFile iconFile = mock(MultipartFile.class);

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(communityId, employeeId, CommunityRole.admin)).thenReturn(true);
        when(fileStorageService.storeCommunityIcon(iconFile)).thenThrow(new IOException("fail"));

        assertThatThrownBy(() -> service.updateCommunityIcon(communityId, iconFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка загрузки файла иконки сообщества");
    }

    @Test
    void leaveCommunity_success() {
        Integer communityId = 1, employeeId = 2;
        Community community = new Community();
        community.setId(communityId);
        Employee creator = new Employee();
        creator.setId(3);
        community.setCreator(creator);

        CommunityMember member = new CommunityMember();
        CommunityMemberId memberId = new CommunityMemberId();
        memberId.setCommunityId(communityId);
        memberId.setEmployeeId(employeeId);
        member.setId(memberId);

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityMemberRepository.findById(any())).thenReturn(Optional.of(member));

        LeaveCommunityResponse resp = service.leaveCommunity(communityId);
        assertThat(resp.getMessage()).contains("успешно покинули");
        verify(communityMemberRepository).delete(any());
    }

    @Test
    void processMembershipRequest_approve() {
        Integer requestId = 7, employeeId = 1;
        CommunityMembershipRequest req = new CommunityMembershipRequest();
        req.setId(requestId);
        req.setStatus(RequestStatus.pending);
        Community community = new Community();
        community.setId(8);
        Employee requester = new Employee();
        requester.setId(3);

        req.setCommunity(community);
        req.setEmployee(requester);

        when(membershipRequestRepository.findById(requestId)).thenReturn(Optional.of(req));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(community.getId(), employeeId, CommunityRole.admin)).thenReturn(true);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));

        service.processMembershipRequest(requestId, true);
        verify(membershipRequestRepository).save(req);
        verify(communityMemberRepository).save(any());
        assertThat(req.getStatus()).isEqualTo(RequestStatus.approved);
    }

    @Test
    void getCommunities_returnsPage() {
        when(communityRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Community())));
        when(mapper.toCommunityDto(any())).thenReturn(new CommunityDto());

        Page<CommunityDto> page = service.getCommunities(Pageable.unpaged());
        assertThat(page).hasSize(1);
    }

}
