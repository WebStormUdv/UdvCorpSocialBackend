package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.backend.UdvCorpSocialBackend.model.Community;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Like;
import ru.backend.UdvCorpSocialBackend.model.Post;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMemberRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.LikeRepository;
import ru.backend.UdvCorpSocialBackend.repository.PostRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LikeService Tests")
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CommunityMemberRepository communityMemberRepository;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private LikeService likeService;

    private Post testPost;
    private Community testCommunity;

    @BeforeEach
    void setUp() {
        Employee testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setEmail("test@mail.ru");
        testEmployee.setFullName("Test User");

        testPost = new Post();
        testPost.setId(1);
        testPost.setContent("Test post");

        testCommunity = new Community();
        testCommunity.setId(1);
        testCommunity.setName("Test Community");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@mail.ru", null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(employeeRepository.findByEmail("test@mail.ru"))
                .thenReturn(Optional.of(testEmployee));
        when(employeeRepository.findById(1))
                .thenReturn(Optional.of(testEmployee));
    }

    @Test
    @DisplayName("Should successfully add like to public post")
    void addLike_PublicPost_Success() {
        // Given
        Integer postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByPostIdAndEmployeeId(postId, 1)).thenReturn(false);

        // When
        likeService.addLike(postId);

        // Then
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("Should successfully add like to community post when user is member")
    void addLike_CommunityPost_UserIsMember_Success() {
        // Given
        Integer postId = 1;
        testPost.setCommunity(testCommunity);

        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeId(1, 1))
                .thenReturn(true);
        when(likeRepository.existsByPostIdAndEmployeeId(postId, 1)).thenReturn(false);

        // When
        likeService.addLike(postId);

        // Then
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(communityMemberRepository, times(1))
                .existsByCommunityIdAndEmployeeId(1, 1);
    }

    @Test
    @DisplayName("Should throw SecurityException when trying to like community post without membership")
    void addLike_CommunityPost_UserNotMember_ThrowsSecurityException() {
        // Given
        Integer postId = 1;
        testPost.setCommunity(testCommunity);

        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeId(1, 1))
                .thenReturn(false);

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> likeService.addLike(postId));

        assertEquals("Вы не являетесь участником сообщества этого поста",
                exception.getMessage());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to like post twice")
    void addLike_AlreadyLiked_ThrowsIllegalStateException() {
        // Given
        Integer postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByPostIdAndEmployeeId(postId, 1)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> likeService.addLike(postId));

        assertEquals("Вы уже поставили лайк на этот пост", exception.getMessage());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when post not found")
    void addLike_PostNotFound_ThrowsEntityNotFoundException() {
        // Given
        Integer postId = 999;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.addLike(postId));

        assertEquals("Пост с ID 999 не найден", exception.getMessage());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when current user not found")
    void addLike_CurrentUserNotFound_ThrowsEntityNotFoundException() {
        // Given
        Integer postId = 1;
        when(employeeRepository.findByEmail("test@mail.ru")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.addLike(postId));

        assertTrue(exception.getMessage().contains("Текущий пользователь с email"));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("Should successfully remove like from public post")
    void removeLike_PublicPost_Success() {
        // Given
        Integer postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByPostIdAndEmployeeId(postId, 1)).thenReturn(true);

        // When
        likeService.removeLike(postId);

        // Then
        verify(likeRepository, times(1)).deleteByPostIdAndEmployeeId(postId, 1);
    }

    @Test
    @DisplayName("Should successfully remove like from community post when user is member")
    void removeLike_CommunityPost_UserIsMember_Success() {
        // Given
        Integer postId = 1;
        testPost.setCommunity(testCommunity);

        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeId(1, 1))
                .thenReturn(true);
        when(likeRepository.existsByPostIdAndEmployeeId(postId, 1)).thenReturn(true);

        // When
        likeService.removeLike(postId);

        // Then
        verify(likeRepository, times(1)).deleteByPostIdAndEmployeeId(postId, 1);
    }

    @Test
    @DisplayName("Should throw SecurityException when trying to unlike community post without membership")
    void removeLike_CommunityPost_UserNotMember_ThrowsSecurityException() {
        // Given
        Integer postId = 1;
        testPost.setCommunity(testCommunity);

        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(communityMemberRepository.existsByCommunityIdAndEmployeeId(1, 1))
                .thenReturn(false);

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> likeService.removeLike(postId));

        assertEquals("Вы не являетесь участником сообщества этого поста",
                exception.getMessage());
        verify(likeRepository, never()).deleteByPostIdAndEmployeeId(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to remove non-existent like")
    void removeLike_NotLiked_ThrowsIllegalStateException() {
        // Given
        Integer postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
        when(likeRepository.existsByPostIdAndEmployeeId(postId, 1)).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> likeService.removeLike(postId));

        assertEquals("Вы не лайкали этот пост", exception.getMessage());
        verify(likeRepository, never()).deleteByPostIdAndEmployeeId(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when trying to remove like from non-existent post")
    void removeLike_PostNotFound_ThrowsEntityNotFoundException() {
        // Given
        Integer postId = 999;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.removeLike(postId));

        assertEquals("Пост с ID 999 не найден", exception.getMessage());
        verify(likeRepository, never()).deleteByPostIdAndEmployeeId(anyInt(), anyInt());
    }
}
