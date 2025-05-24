package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByEmployeeIdAndTimestampAfter(Integer employeeId, LocalDateTime startOfDay);
    Page<Comment> findByPostId(Integer postId, Pageable pageable);
}
