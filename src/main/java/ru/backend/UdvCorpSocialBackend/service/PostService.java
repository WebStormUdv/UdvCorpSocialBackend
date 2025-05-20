package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.PostCreateDto;
import ru.backend.UdvCorpSocialBackend.dto.PostDto;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Post;
import ru.backend.UdvCorpSocialBackend.model.enums.PostType;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.LikeRepository;
import ru.backend.UdvCorpSocialBackend.repository.PostRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private static final int MAX_POSTS_PER_DAY = 5;
    private static final long MAX_MEDIA_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    private final PostRepository postRepository;
    private final EmployeeRepository employeeRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public PostService(PostRepository postRepository, EmployeeRepository employeeRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.employeeRepository = employeeRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public PostDto createPost(PostCreateDto postCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));

        // Check daily post limit
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        long postCount = postRepository.countByEmployeeIdAndTimestampBetween(
                employee.getId(), startOfDay, endOfDay);
        if (postCount >= MAX_POSTS_PER_DAY) {
            throw new IllegalStateException("Daily post limit of " + MAX_POSTS_PER_DAY + " reached");
        }

        // Validate media size (mock check, assumes media is accessible)
        if (postCreateDto.getMediaUrl() != null && !postCreateDto.getMediaUrl().isEmpty()) {
            // In a real scenario, check the actual file size via HTTP HEAD request or storage service
            // Here we assume the size is valid for simplicity
            // TODO
        }

        Post post = new Post();
        post.setEmployee(employee);
        post.setCommunity(null); // Global post, no community
        post.setContent(postCreateDto.getContent());
        post.setMediaUrl(postCreateDto.getMediaUrl());
        post.setMediaType(postCreateDto.getMediaType());
        post.setType(postCreateDto.getType());

        Post savedPost = postRepository.save(post);
        logger.info("Post created with ID: {} by employee: {}", savedPost.getId(), email);

        return mapToDto(savedPost);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPosts(PostType type, String sortBy, String sortDirection, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts;
        if (type != null) {
            posts = postRepository.findByTypeAndCommunityIsNull(type, pageable);
        } else {
            posts = postRepository.findByCommunityIsNull(pageable);
        }

        logger.info("Получен список постов, страница: {}, размер: {}, тип: {}", page, size, type);
        return posts.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Integer id) {
        Post post = postRepository.findByIdAndCommunityIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + id + " не найден или не является глобальным"));
        logger.info("Получен пост с ID: {}", id);
        return mapToDto(post);
    }

    @Transactional
    public PostDto updatePost(Integer id, PostCreateDto postCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник не найден с email: " + email));

        Post post = postRepository.findByIdAndCommunityIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + id + " не найден или не является глобальным"));

        if (!post.getEmployee().getId().equals(employee.getId())) {
            throw new SecurityException("Только автор может обновлять пост");
        }

        // Валидация размера медиа (заглушка)
        if (postCreateDto.getMediaUrl() != null && !postCreateDto.getMediaUrl().isEmpty()) {
            // TODO: Реализовать проверку размера файла
        }

        post.setContent(postCreateDto.getContent());
        post.setMediaUrl(postCreateDto.getMediaUrl());
        post.setMediaType(postCreateDto.getMediaType());
        post.setType(postCreateDto.getType());

        Post updatedPost = postRepository.save(post);
        logger.info("Пост с ID: {} обновлен сотрудником: {}", id, email);
        return mapToDto(updatedPost);
    }

    @Transactional
    public void deletePost(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник не найден с email: " + email));

        Post post = postRepository.findByIdAndCommunityIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + id + " не найден или не является глобальным"));

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        boolean isAuthor = post.getEmployee().getId().equals(employee.getId());

        if (!isAuthor && !isAdmin) {
            throw new SecurityException("Только автор или админ могут удалять пост");
        }

        postRepository.delete(post);
        logger.info("Пост с ID: {} удален сотрудником: {}", id, email);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByEmployeeId(Integer employeeId, PostType type, String sortBy, String sortDirection, int page, int size) {
        // Если employeeId не указан, использовать ID текущего пользователя
        Integer targetEmployeeId = employeeId;
        if (employeeId == null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee currentEmployee = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Сотрудник не найден с email: " + email));
            targetEmployeeId = currentEmployee.getId();
        } else {
            // Проверка существования сотрудника
            employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts;
        if (type != null) {
            posts = postRepository.findByEmployeeIdAndTypeAndCommunityIsNull(targetEmployeeId, type, pageable);
        } else {
            posts = postRepository.findByEmployeeIdAndCommunityIsNull(targetEmployeeId, pageable);
        }

        logger.info("Получен список постов для сотрудника ID: {}, страница: {}, размер: {}, тип: {}",
                targetEmployeeId, page, size, type);
        return posts.map(this::mapToDto);
    }

    private PostDto mapToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setEmployeeId(post.getEmployee().getId());
        dto.setEmployeeFullName(post.getEmployee().getFullName());
        dto.setContent(post.getContent());
        dto.setMediaUrl(post.getMediaUrl());
        dto.setMediaType(post.getMediaType());
        dto.setType(post.getType());
        dto.setTimestamp(post.getTimestamp());

        Integer currentEmployeeId = getCurrentEmployeeId();
        dto.setIsLiked(likeRepository.existsByPostIdAndEmployeeId(post.getId(), currentEmployeeId));
        dto.setLikesCount(likeRepository.countByPostId(post.getId()));

        return dto;
    }

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() -> new EntityNotFoundException("Текущий пользователь с email " + email + " не найден"));
    }
}