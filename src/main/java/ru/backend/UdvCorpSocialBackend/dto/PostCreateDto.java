package ru.backend.UdvCorpSocialBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.PostType;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @Size(max = 255, message = "Media URL must not exceed 255 characters")
    @Pattern(
            regexp = "^(http|https)://.*\\.(jpg|jpeg|png|mp4|pdf|docx)$|^$",
            message = "Media URL must be a valid URL ending with .jpg, .jpeg, .png, .mp4, .pdf, or .docx"
    )
    private String mediaUrl;

    @Size(max = 50, message = "Media type must not exceed 50 characters")
    @Pattern(
            regexp = "^(image/jpeg|image/png|video/mp4|application/pdf|application/vnd.openxmlformats-officedocument.wordprocessingml.document)$|^$",
            message = "Media type must be one of: image/jpeg, image/png, video/mp4, application/pdf, application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
    private String mediaType;

    @NotNull(message = "Post type is required")
    private PostType type;
}
