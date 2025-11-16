package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.repository.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final EmployeeRepository employeeRepository;
    private final CommunityMemberRepository communityMemberRepository;

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() -> new EntityNotFoundException("Текущий пользователь с email " + email + " не найден"));
    }

    @Transactional
    public void addLike(Integer postId) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} attempting to like post {}", employeeId, postId);

        Post post = getPost(postId);

        if (post.getCommunity() != null) {
            Integer communityId = post.getCommunity().getId();
            if (!communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)) {
                log.warn("Employee {} tried to like post {} in community {} without membership",
                        employeeId, postId, communityId);
                throw new SecurityException("Вы не являетесь участником сообщества этого поста");
            }
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee with ID {} not found", employeeId);
                    return new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден");
                });

        if (likeRepository.existsByPostIdAndEmployeeId(postId, employeeId)) {
            log.warn("Employee {} tried to like post {} again", employeeId, postId);
            throw new IllegalStateException("Вы уже поставили лайк на этот пост");
        }

        Like like = new Like();
        like.setPost(post);
        like.setEmployee(employee);
        likeRepository.save(like);

        log.info("Employee {} successfully liked post {}", employeeId, postId);
    }

    @Transactional
    public void removeLike(Integer postId) {
        Integer employeeId = getCurrentEmployeeId();
        log.info("Employee {} attempting to remove like from post {}", employeeId, postId);

        Post post = getPost(postId);

        if (post.getCommunity() != null) {
            Integer communityId = post.getCommunity().getId();
            if (!communityMemberRepository.existsByCommunityIdAndEmployeeId(communityId, employeeId)) {
                log.warn("Employee {} tried to unlike post {} in community {} without membership",
                        employeeId, postId, communityId);
                throw new SecurityException("Вы не являетесь участником сообщества этого поста");
            }
        }

        if (!likeRepository.existsByPostIdAndEmployeeId(postId, employeeId)) {
            log.warn("Employee {} tried to remove non-existent like from post {}", employeeId, postId);
            throw new IllegalStateException("Вы не лайкали этот пост");
        }

        likeRepository.deleteByPostIdAndEmployeeId(postId, employeeId);
        log.info("Employee {} successfully removed like from post {}", employeeId, postId);
    }

    private Post getPost(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post with ID {} not found", postId);
                    return new EntityNotFoundException("Пост с ID " + postId + " не найден");
                });
    }
}