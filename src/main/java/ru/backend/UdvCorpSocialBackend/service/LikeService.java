package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.repository.*;

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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));

        if (post.getCommunity() != null && !communityMemberRepository.existsByCommunityIdAndEmployeeId(
                post.getCommunity().getId(), employeeId)) {
            throw new SecurityException("Вы не являетесь участником сообщества этого поста");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        if (likeRepository.existsByPostIdAndEmployeeId(postId, employeeId)) {
            throw new IllegalStateException("Вы уже лайкнули этот пост");
        }

        Like like = new Like();
        like.setPost(post);
        like.setEmployee(employee);
        likeRepository.save(like);
    }

    @Transactional
    public void removeLike(Integer postId) {
        Integer employeeId = getCurrentEmployeeId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));

        if (post.getCommunity() != null && !communityMemberRepository.existsByCommunityIdAndEmployeeId(
                post.getCommunity().getId(), employeeId)) {
            throw new SecurityException("Вы не являетесь участником сообщества этого поста");
        }

        if (!likeRepository.existsByPostIdAndEmployeeId(postId, employeeId)) {
            throw new IllegalStateException("Вы не лайкали этот пост");
        }

        likeRepository.deleteByPostIdAndEmployeeId(postId, employeeId);
    }
}