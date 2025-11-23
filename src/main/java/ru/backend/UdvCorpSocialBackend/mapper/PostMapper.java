package ru.backend.UdvCorpSocialBackend.mapper;

import org.springframework.stereotype.Component;
import ru.backend.UdvCorpSocialBackend.dto.post.PostDto;
import ru.backend.UdvCorpSocialBackend.model.Post;
import ru.backend.UdvCorpSocialBackend.repository.CommentRepository;
import ru.backend.UdvCorpSocialBackend.repository.LikeRepository;

@Component
public class PostMapper {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public PostMapper(
            LikeRepository likeRepository,
            CommentRepository commentRepository
    ) {
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    public PostDto toPostDto(Post post, Integer currentEmployeeId) {
        PostDto dto = new PostDto();

        dto.setId(post.getId());
        dto.setEmployeeId(post.getEmployee().getId());
        dto.setEmployeeFullName(post.getEmployee().getFullName());
        dto.setEmployeePosition(post.getEmployee().getPosition());

        if (post.getCommunity() != null) {
            dto.setCommunityId(post.getCommunity().getId());
        }

        dto.setContent(post.getContent());
        dto.setMediaUrl(post.getMediaUrl());
        dto.setMediaType(post.getMediaType());
        dto.setType(post.getType());
        dto.setTimestamp(post.getTimestamp());

        dto.setIsLiked(likeRepository.existsByPostIdAndEmployeeId(post.getId(), currentEmployeeId));
        dto.setLikesCount(likeRepository.countByPostId(post.getId()));
        dto.setCommentsCount(commentRepository.countByPostId(post.getId()));

        return dto;
    }
}
