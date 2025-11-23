package ru.backend.UdvCorpSocialBackend.dto.post;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.PostType;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Integer id;
    private Integer employeeId;
    private String employeeFullName;
    private String employeePosition;
    private Integer communityId;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private PostType type;
    private LocalDateTime timestamp;
    private boolean isLiked;
    private long likesCount;
    private long commentsCount;

    public void setIsLiked(boolean b) {
        this.isLiked = b;
    }
}
