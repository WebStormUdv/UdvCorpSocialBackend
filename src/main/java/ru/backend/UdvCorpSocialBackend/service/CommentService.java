package ru.backend.UdvCorpSocialBackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.backend.UdvCorpSocialBackend.model.Comment;
import ru.backend.UdvCorpSocialBackend.model.Employee;
import ru.backend.UdvCorpSocialBackend.model.Post;
import ru.backend.UdvCorpSocialBackend.model.enums.RoleType;
import ru.backend.UdvCorpSocialBackend.repository.CommentRepository;
import ru.backend.UdvCorpSocialBackend.repository.EmployeeRepository;
import ru.backend.UdvCorpSocialBackend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EmployeeRepository employeeRepository;

    private Integer getCurrentEmployeeId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .map(Employee::getId)
                .orElseThrow(() -> new EntityNotFoundException("Текущий пользователь с email " + email + " не найден"));
    }

    @Transactional
    public Comment addComment(Integer postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + postId + " не найден"));

        Integer employeeId = getCurrentEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Сотрудник с ID " + employeeId + " не найден"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setEmployee(employee);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPostId(Integer postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Пост с ID " + postId + " не найден");
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

        if (!comment.getEmployee().getId().equals(employeeId) && employee.getRole() != RoleType.admin) {
            throw new IllegalStateException("Вы не можете удалить этот комментарий");
        }

        commentRepository.delete(comment);
    }
}