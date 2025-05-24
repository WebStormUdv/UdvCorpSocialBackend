package ru.backend.UdvCorpSocialBackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {
    private Long id;
    private Long postId;
    private EmployeeDTO employee;
    private String content;
    private LocalDateTime timestamp;
}