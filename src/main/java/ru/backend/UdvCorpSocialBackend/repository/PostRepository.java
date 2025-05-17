package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Post;
import ru.backend.UdvCorpSocialBackend.model.enums.PostType;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    long countByEmployeeIdAndTimestampBetween(Integer employeeId, LocalDateTime start, LocalDateTime end);

    Page<Post> findByTypeAndCommunityIsNull(PostType type, Pageable pageable);

    Page<Post> findByCommunityIsNull(Pageable pageable);

    Optional<Post> findByIdAndCommunityIsNull(Integer id);

    Page<Post> findByEmployeeIdAndCommunityIsNull(Integer employeeId, Pageable pageable);

    Page<Post> findByEmployeeIdAndTypeAndCommunityIsNull(Integer employeeId, PostType type, Pageable pageable);
}
