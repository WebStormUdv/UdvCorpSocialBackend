package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.PostType;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Integer id;
    private Integer employeeId;
    private String employeeFullName;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private PostType type;
    private LocalDateTime timestamp;
    private boolean isLiked;
    private long likesCount;

    public void setIsLiked(boolean b) {
        this.isLiked = b;
    }
}
