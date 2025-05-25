package ru.backend.UdvCorpSocialBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ru.backend.UdvCorpSocialBackend.model.enums.PostType;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    private MultipartFile mediaFile;

    private String mediaType;

    @NotNull(message = "Post type is required")
    private PostType type;

    private Integer communityId; // Optional, for community-specific posts
}