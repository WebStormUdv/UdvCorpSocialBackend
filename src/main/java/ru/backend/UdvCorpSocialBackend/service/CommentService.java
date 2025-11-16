package ru.backend.UdvCorpSocialBackend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.model.*;
import ru.backend.UdvCorpSocialBackend.model.CommunityRole;
import ru.backend.UdvCorpSocialBackend.model.RoleType;
import ru.backend.UdvCorpSocialBackend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EmployeeRepository employeeRepository;
    private final CommunityMemberRepository communityMemberRepository;

    private static final int MAX_COMMENTS_PER_DAY = 50;

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() -> new EntityNotFoundException("Текущий пользователь с email " + email + " не найден"));
    }

    @Transactional
    public Comment addComment(Integer postId, String content) {
        Integer employeeId = getCurrentEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));

        // Проверка членства в сообществе
        if (post.getCommunity() != null && !communityMemberRepository.existsByCommunityIdAndEmployeeId(
                post.getCommunity().getId(), employeeId)) {
            throw new SecurityException("Вы не являетесь участником сообщества этого поста");
        }

        // Проверка лимита комментариев
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        if (commentRepository.countByEmployeeIdAndTimestampAfter(employeeId, startOfDay) >= MAX_COMMENTS_PER_DAY) {
            throw new IllegalStateException("Достигнут дневной лимит комментариев: " + MAX_COMMENTS_PER_DAY);
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setEmployee(employee);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPostId(Integer postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));

        Integer employeeId = getCurrentEmployeeId();
        if (post.getCommunity() != null && !communityMemberRepository.existsByCommunityIdAndEmployeeId(
                post.getCommunity().getId(), employeeId)) {
            throw new SecurityException("Вы не являетесь участником сообщества этого поста");
        }

        return commentRepository.findByPostId(postId, pageable);
    }

    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Integer employeeId = getCurrentEmployeeId();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с ID " + commentId + " не найден"));

        if (!comment.getEmployee().getId().equals(employeeId)) {
            throw new IllegalStateException("Вы не можете редактировать этот комментарий");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void removeComment(Long commentId) {
        Integer employeeId = getCurrentEmployeeId();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с ID " + commentId + " не найден"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        boolean isCommunityAdmin = comment.getPost().getCommunity() != null &&
                communityMemberRepository.existsByCommunityIdAndEmployeeIdAndRole(
                        comment.getPost().getCommunity().getId(), employeeId, CommunityRole.admin);

        if (!comment.getEmployee().getId().equals(employeeId) && employee.getRole() != RoleType.admin && !isCommunityAdmin) {
            throw new IllegalStateException("Вы не можете удалить этот комментарий");
        }

        commentRepository.delete(comment);
    }
}