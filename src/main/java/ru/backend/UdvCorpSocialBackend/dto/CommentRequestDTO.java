package ru.backend.UdvCorpSocialBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
    private String content;
}