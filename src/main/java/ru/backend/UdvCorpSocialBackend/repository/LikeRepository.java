package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByPostIdAndEmployeeId(Integer postId, Integer employeeId);
    void deleteByPostIdAndEmployeeId(Integer postId, Integer employeeId);
    long countByPostId(Integer postId);
}
