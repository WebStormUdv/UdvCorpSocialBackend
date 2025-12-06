package ru.backend.UdvCorpSocialBackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.backend.UdvCorpSocialBackend.model.Post;
import ru.backend.UdvCorpSocialBackend.model.PostType;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findByEmployeeIdAndCommunityIsNull(Integer employeeId, Pageable pageable);

    Page<Post> findByEmployeeIdAndTypeAndCommunityIsNull(Integer employeeId, PostType type, Pageable pageable);

    Page<Post> findByCommunityId(Integer communityId, Pageable pageable);

    long countByEmployeeIdAndCommunityIdAndTimestampBetween(Integer employeeId, Integer communityId, LocalDateTime start, LocalDateTime end);

    long countByEmployeeIdAndCommunityIsNullAndTimestampBetween(Integer employeeId, LocalDateTime start, LocalDateTime end);

    Page<Post> findByCommunityIdAndType(Integer communityId, PostType type, Pageable pageable);

    @Query(
            "SELECT p FROM Post p " +
                    "LEFT JOIN p.community c " +
                    "LEFT JOIN CommunityMember cm ON cm.community.id = c.id AND cm.employee.id = :employeeId " +
                    "WHERE (p.community IS NULL) OR (cm.employee.id = :employeeId)"
    )
    Page<Post> findGlobalAndMemberCommunityPosts(
            @Param("employeeId") Integer employeeId,
            Pageable pageable
    );

    @Query(
            "SELECT p FROM Post p " +
                    "LEFT JOIN p.community c " +
                    "LEFT JOIN CommunityMember cm ON cm.community.id = c.id AND cm.employee.id = :employeeId " +
                    "WHERE p.type = :type AND ((p.community IS NULL) OR (cm.employee.id = :employeeId))"
    )
    Page<Post> findGlobalAndMemberCommunityPostsByType(
            @Param("employeeId") Integer employeeId,
            @Param("type") PostType type,
            Pageable pageable
    );
}
