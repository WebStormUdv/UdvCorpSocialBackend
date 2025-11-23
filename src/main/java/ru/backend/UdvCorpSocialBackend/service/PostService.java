package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.dto.post.PostCreateDto;
import ru.backend.UdvCorpSocialBackend.dto.post.PostDto;
import ru.backend.UdvCorpSocialBackend.mapper.PostMapper;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.repository.CommunityMemberRepository;
import ru.backend.UdvCorpSocialBackend.repository.CommunityRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.PostRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PostService {

    private static final int MAX_POSTS_PER_DAY = 5;
    private static final long MAX_MEDIA_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    private final PostRepository postRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final PostMapper mapper;

    @Autowired
    public PostService(
            PostRepository postRepository,
            EmployeeRepository employeeRepository,
            FileStorageService fileStorageService,
            CommunityRepository communityRepository,
            CommunityMemberRepository communityMemberRepository,
            PostMapper mapper
    ) {
        this.postRepository = postRepository;
        this.employeeRepository = employeeRepository;
        this.fileStorageService = fileStorageService;
        this.communityRepository = communityRepository;
        this.communityMemberRepository = communityMemberRepository;
        this.mapper = mapper;
    }

    @Transactional
    public PostDto createPost(PostCreateDto postCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Employee {} attempting to create post", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Employee not found with email: {}", email);
                    return new EntityNotFoundException("Employee not found with email: " + email);
                });

        Integer communityId = postCreateDto.getCommunityId();
        Community community = null;

        if (communityId != null) {
            log.debug("Creating community post for community ID: {}", communityId);
            community = communityRepository.findById(communityId)
                    .orElseThrow(() -> {
                        log.error("Community not found with ID: {}", communityId);
                        return new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено");
                    });

            if (!communityMemberRepository
                    .existsByCommunityIdAndEmployeeId(communityId, employee.getId())
            ) {
                log.warn("Employee {} is not a member of community {}", email, communityId);
                throw new SecurityException("Вы не являетесь участником сообщества");
            }

            if (community.getType() == CommunityType.closed &&
                    !communityMemberRepository
                            .existsByCommunityIdAndEmployeeIdAndRole(communityId, employee.getId(), CommunityRole.admin)
            ) {
                log.warn("Employee {} tried to post in closed community {} without admin rights", email, communityId);
                throw new SecurityException("Только администраторы могут создавать посты в закрытом сообществе");
            }
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long postCount = communityId == null
                ? postRepository
                .countByEmployeeIdAndCommunityIsNullAndTimestampBetween(employee.getId(), startOfDay, endOfDay)
                : postRepository
                .countByEmployeeIdAndCommunityIdAndTimestampBetween(employee.getId(), communityId, startOfDay, endOfDay);

        if (postCount >= MAX_POSTS_PER_DAY) {
            log.debug("Employee {} has created {} posts today (limit: {})", email, postCount, MAX_POSTS_PER_DAY);
            throw new IllegalStateException("Достигнут дневной лимит постов: " + MAX_POSTS_PER_DAY);
        }

        Post post = new Post();
        post.setEmployee(employee);
        post.setCommunity(community);
        post.setContent(postCreateDto.getContent());
        post.setType(postCreateDto.getType());

        if (postCreateDto.getMediaFile() != null &&
                !postCreateDto.getMediaFile().isEmpty()
        ) {
            long fileSize = postCreateDto.getMediaFile().getSize();
            log.debug("Processing media file, size: {} bytes", fileSize);

            if (fileSize > MAX_MEDIA_SIZE_BYTES) {
                throw new IllegalStateException("Размер файла превышает лимит в 10 МБ");
            }
            try {
                String mediaUrl = fileStorageService.storeFile(postCreateDto.getMediaFile());
                post.setMediaUrl(mediaUrl);
                post.setMediaType(postCreateDto.getMediaFile().getContentType());
                log.info("Media file stored successfully: {}", mediaUrl);
            } catch (IOException e) {
                log.error("Failed to store media file for post by employee: {}", email, e);
                throw new RuntimeException("Не удалось сохранить медиафайл", e);
            }
        }

        Post savedPost = postRepository.save(post);
        log.info("Post created with ID: {} by employee: {} in community: {}", savedPost.getId(), email, communityId);

        return mapToDto(savedPost);
    }

    @Transactional
    public PostDto updatePost(Integer id, PostCreateDto postCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Employee {} attempting to update post ID: {}", email, id);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Employee not found with email: {}", email);
                    return new EntityNotFoundException("Сотрудник не найден с email: " + email);
                });

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", id);
                    return new EntityNotFoundException("Пост с ID " + id + " не найден");
                });

        if (!post.getEmployee().getId().equals(employee.getId())) {
            log.warn("Employee {} attempted to update post {} owned by employee {}",
                    email, id, post.getEmployee().getId());
            throw new SecurityException("Только автор может обновлять пост");
        }

        if (post.getCommunity() != null) {
            Integer communityId = post.getCommunity().getId();
            log.debug("Checking community permissions for post {} in community {}", id, communityId);

            if (!communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employee.getId())) {
                log.warn("Employee {} is not a member of community {}", email, communityId);
                throw new SecurityException("Вы не являетесь участником сообщества");
            }
            if (post.getCommunity().getType() == CommunityType.closed &&
                    !communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(communityId, employee.getId(), CommunityRole.admin)) {
                log.warn("Employee {} tried to update post in closed community {} without admin rights", email, communityId);
                throw new SecurityException("Только администраторы могут обновлять посты в закрытом сообществе");
            }
        }

        post.setContent(postCreateDto.getContent());
        post.setType(postCreateDto.getType());

        if (postCreateDto.getMediaFile() != null && !postCreateDto.getMediaFile().isEmpty()) {
            long fileSize = postCreateDto.getMediaFile().getSize();
            log.debug("Updating media file for post {}, size: {} bytes", id, fileSize);

            if (fileSize > MAX_MEDIA_SIZE_BYTES) {
                log.warn("Media file too large: {} bytes (max: {})", fileSize, MAX_MEDIA_SIZE_BYTES);
                throw new IllegalStateException("Размер файла превышает лимит в 10 МБ");
            }
            try {
                String mediaUrl = fileStorageService.storeFile(postCreateDto.getMediaFile());
                if (post.getMediaUrl() != null) {
                    log.debug("Deleting old media file: {}", post.getMediaUrl());
                    fileStorageService.deleteFile(post.getMediaUrl());
                }
                post.setMediaUrl(mediaUrl);
                post.setMediaType(postCreateDto.getMediaFile().getContentType());
                log.info("Media file updated successfully for post {}", id);
            } catch (IOException e) {
                log.error("Failed to store media file for post update ID: {}", id, e);
                throw new RuntimeException("Не удалось сохранить медиафайл", e);
            }
        } else if (postCreateDto.getMediaFile() == null) {
            if (post.getMediaUrl() != null) {
                log.debug("Removing media file from post {}", id);
                fileStorageService.deleteFile(post.getMediaUrl());
            }
            post.setMediaUrl(null);
            post.setMediaType(null);
        }

        Post updatedPost = postRepository.save(post);
        log.info("Post ID: {} updated by employee: {}", id, email);
        return mapToDto(updatedPost);
    }

    @Transactional
    public void deletePost(Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Employee {} attempting to delete post ID: {}", email, id);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Employee not found with email: {}", email);
                    return new EntityNotFoundException("Сотрудник не найден с email: " + email);
                });

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", id);
                    return new EntityNotFoundException("Пост с ID " + id + " не найден");
                });

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        boolean isAuthor = post.getEmployee().getId().equals(employee.getId());
        boolean isCommunityAdmin = post.getCommunity() != null &&
                communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(post.getCommunity().getId(), employee.getId(), CommunityRole.admin);

        log.debug("Delete permissions for post {}: isAdmin={}, isAuthor={}, isCommunityAdmin={}",
                id, isAdmin, isAuthor, isCommunityAdmin);

        if (!isAuthor && !isAdmin && !isCommunityAdmin) {
            log.warn("Employee {} denied permission to delete post {}", email, id);
            throw new SecurityException("Только автор, админ или администратор сообщества могут удалять пост");
        }

        if (post.getMediaUrl() != null) {
            log.debug("Deleting media file for post {}: {}", id, post.getMediaUrl());
            fileStorageService.deleteFile(post.getMediaUrl());
        }

        postRepository.delete(post);
        log.info("Post ID: {} deleted by employee: {}", id, email);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPosts(
            PostType type, String sortBy, String sortDirection,
            int page, int size
    ) {
        log.debug("Fetching posts: type={}, sortBy={}, sortDirection={}, page={}, size={}",
                type, sortBy, sortDirection, page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts;
        if (type != null) {
            posts = postRepository.findByTypeAndCommunityIsNull(type, pageable);
        } else {
            posts = postRepository.findByCommunityIsNull(pageable);
        }

        log.info("Retrieved {} posts (page {}/{})", posts.getNumberOfElements(), page + 1, posts.getTotalPages());
        return posts.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Integer id) {
        log.debug("Fetching post by ID: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", id);
                    return new EntityNotFoundException("Пост с ID " + id + " не найден");
                });

        if (post.getCommunity() != null) {
            Integer employeeId = getCurrentEmployeeId();
            log.debug("Checking access to community post {} for employee {}", id, employeeId);

            if (post.getCommunity().getType() == CommunityType.closed &&
                    !communityMemberRepository
                            .existsByCommunityIdAndEmployeeId(post.getCommunity().getId(), employeeId)
            ) {
                log.warn("Employee {} denied access to closed community post {}", employeeId, id);
                throw new SecurityException("Вы не имеете доступа к постам этого закрытого сообщества");
            }
        }

        log.info("Post ID: {} retrieved successfully", id);
        return mapToDto(post);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getPostsByEmployeeId(
            Integer employeeId, PostType type, String sortBy,
            String sortDirection, int page, int size
    ) {
        Integer targetEmployeeId = employeeId;
        if (employeeId == null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee currentEmployee = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Сотрудник не найден с email: " + email));
            targetEmployeeId = currentEmployee.getId();
            log.debug("Fetching posts for current employee: {}", targetEmployeeId);
        } else {
            employeeRepository.findById(employeeId)
                    .orElseThrow(() -> {
                        log.error("Employee not found with ID: {}", employeeId);
                        return new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден");
                    });
            log.debug("Fetching posts for employee ID: {}", targetEmployeeId);
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts;
        if (type != null) {
            posts = postRepository.findByEmployeeIdAndTypeAndCommunityIsNull(targetEmployeeId, type, pageable);
        } else {
            posts = postRepository.findByEmployeeIdAndCommunityIsNull(targetEmployeeId, pageable);
        }

        log.info("Retrieved {} posts for employee {} (page {}/{})",
                posts.getNumberOfElements(), targetEmployeeId, page + 1, posts.getTotalPages());
        return posts.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getCommunityPosts(
            Integer communityId, PostType type, String sortBy,
            String sortDirection, int page, int size
    ) {
        log.debug("Fetching posts for community ID: {}", communityId);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> {
                    log.error("Community not found with ID: {}", communityId);
                    return new EntityNotFoundException("Сообщество с ID " + communityId + " не найдено");
                });

        Integer employeeId = getCurrentEmployeeId();
        if (community.getType() == CommunityType.closed &&
                !communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)) {
            log.warn("Employee {} denied access to closed community {} posts", employeeId, communityId);
            throw new SecurityException("Вы не имеете доступа к постам этого закрытого сообщества");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts;
        if (type != null) {
            posts = postRepository.findByCommunityIdAndType(communityId, type, pageable);
        } else {
            posts = postRepository.findByCommunityId(communityId, pageable);
        }

        log.info("Retrieved {} posts for community {} (page {}/{})",
                posts.getNumberOfElements(), communityId, page + 1, posts.getTotalPages());
        return posts.map(this::mapToDto);
    }

    private PostDto mapToDto(Post post) {
        Integer id = getCurrentEmployeeId();
        return mapper.toPostDto(post, id);
    }

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() -> new EntityNotFoundException("Текущий пользователь с email " + email + " не найден"));
    }
}