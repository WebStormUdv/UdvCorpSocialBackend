package ru.backend.UdvCorpSocialBackend.dto.comment;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeDTO;

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