package ru.backend.UdvCorpSocialBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.backend.UdvCorpSocialBackend.dto.post.PostDto;
import ru.backend.UdvCorpSocialBackend.mapper.PostMapper;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostService postService;

    private Post globalPost;
    private Post communityPost;
    private PostDto globalPostDto;
    private PostDto communityPostDto;

    @BeforeEach
    void setUp() {
        // Setup test employee
        Employee testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setEmail("test@example.com");
        testEmployee.setFullName("Test User");
        testEmployee.setPosition("Developer");

        // Setup test community
        Community testCommunity = new Community();
        testCommunity.setId(1);
        testCommunity.setName("Test Community");
        testCommunity.setType(CommunityType.open);

        // Setup global post
        globalPost = new Post();
        globalPost.setId(1);
        globalPost.setEmployee(testEmployee);
        globalPost.setCommunity(null);
        globalPost.setContent("Global post content");
        globalPost.setType(PostType.news);
        globalPost.setTimestamp(LocalDateTime.now());

        // Setup community post
        communityPost = new Post();
        communityPost.setId(2);
        communityPost.setEmployee(testEmployee);
        communityPost.setCommunity(testCommunity);
        communityPost.setContent("Community post content");
        communityPost.setType(PostType.discussion);
        communityPost.setTimestamp(LocalDateTime.now());

        // Setup DTOs
        globalPostDto = new PostDto();
        globalPostDto.setId(1);
        globalPostDto.setEmployeeId(1);
        globalPostDto.setEmployeeFullName("Test User");
        globalPostDto.setCommunityId(null);
        globalPostDto.setContent("Global post content");
        globalPostDto.setType(PostType.news);

        communityPostDto = new PostDto();
        communityPostDto.setId(2);
        communityPostDto.setEmployeeId(1);
        communityPostDto.setEmployeeFullName("Test User");
        communityPostDto.setCommunityId(1);
        communityPostDto.setContent("Community post content");
        communityPostDto.setType(PostType.discussion);

        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // Mock employee repository
        when(employeeRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testEmployee));
    }

    @Test
    void getPosts_WithoutTypeFilter_ShouldReturnAllPosts() {
        // Arrange
        List<Post> posts = Arrays.asList(globalPost, communityPost);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(postRepository.findGlobalAndMemberCommunityPosts(
                eq(1),
                any(Pageable.class)
        )).thenReturn(postPage);

        when(postMapper.toPostDto(globalPost, 1)).thenReturn(globalPostDto);
        when(postMapper.toPostDto(communityPost, 1)).thenReturn(communityPostDto);

        // Act
        Page<PostDto> result = postService.getPosts(null, "timestamp", "DESC", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getCommunityId() == null));
        assertTrue(result.getContent().stream().anyMatch(p -> p.getCommunityId() != null));

        verify(postRepository).findGlobalAndMemberCommunityPosts(eq(1), any(Pageable.class));
        verify(postMapper, times(2)).toPostDto(any(Post.class), eq(1));
    }

    @Test
    void getPosts_WithTypeFilter_ShouldReturnFilteredPosts() {
        // Arrange
        List<Post> posts = Collections.singletonList(globalPost);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(postRepository.findGlobalAndMemberCommunityPostsByType(
                eq(1),
                eq(PostType.news),
                any(Pageable.class)
        )).thenReturn(postPage);

        when(postMapper.toPostDto(globalPost, 1)).thenReturn(globalPostDto);

        // Act
        Page<PostDto> result = postService.getPosts(PostType.news, "timestamp", "DESC", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(PostType.news, result.getContent().getFirst().getType());

        verify(postRepository).findGlobalAndMemberCommunityPostsByType(
                eq(1),
                eq(PostType.news),
                any(Pageable.class)
        );
        verify(postMapper).toPostDto(globalPost, 1);
    }

    @Test
    void getPosts_WithCustomSorting_ShouldApplyCorrectSort() {
        // Arrange
        List<Post> posts = Collections.singletonList(globalPost);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(postRepository.findGlobalAndMemberCommunityPosts(
                eq(1),
                any(Pageable.class)
        )).thenReturn(postPage);

        when(postMapper.toPostDto(globalPost, 1)).thenReturn(globalPostDto);

        // Act
        Page<PostDto> result = postService.getPosts(null, "id", "ASC", 0, 10);

        // Assert
        assertNotNull(result);
        verify(postRepository).findGlobalAndMemberCommunityPosts(
                eq(1),
                argThat(pageable ->
                        pageable.getSort().getOrderFor("id") != null &&
                                Objects.requireNonNull(pageable.getSort().getOrderFor("id")).getDirection() == Sort.Direction.ASC
                )
        );
    }

    @Test
    void getPosts_WithPagination_ShouldReturnCorrectPage() {
        // Arrange
        List<Post> posts = Arrays.asList(globalPost, communityPost);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(1, 5), 12);

        when(postRepository.findGlobalAndMemberCommunityPosts(
                eq(1),
                any(Pageable.class)
        )).thenReturn(postPage);

        when(postMapper.toPostDto(any(Post.class), eq(1)))
                .thenReturn(globalPostDto, communityPostDto);

        // Act
        Page<PostDto> result = postService.getPosts(null, "timestamp", "DESC", 1, 5);

        // Assert
        assertNotNull(result);
        assertEquals(12, result.getTotalElements());
        assertEquals(2, result.getNumberOfElements());
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());

        verify(postRepository).findGlobalAndMemberCommunityPosts(
                eq(1),
                argThat(pageable ->
                        pageable.getPageNumber() == 1 &&
                                pageable.getPageSize() == 5
                )
        );
    }

    @Test
    void getPosts_WithEmptyResult_ShouldReturnEmptyPage() {
        // Arrange
        Page<Post> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(postRepository.findGlobalAndMemberCommunityPosts(
                eq(1),
                any(Pageable.class)
        )).thenReturn(emptyPage);

        // Act
        Page<PostDto> result = postService.getPosts(null, "timestamp", "DESC", 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(postRepository).findGlobalAndMemberCommunityPosts(eq(1), any(Pageable.class));
        verify(postMapper, never()).toPostDto(any(Post.class), anyInt());
    }

    @Test
    void getPosts_WhenEmployeeNotFound_ShouldThrowException() {
        // Arrange
        when(employeeRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            postService.getPosts(null, "timestamp", "DESC", 0, 10);
        });

        verify(postRepository, never()).findGlobalAndMemberCommunityPosts(anyInt(), any(Pageable.class));
    }
}
