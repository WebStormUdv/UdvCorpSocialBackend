package ru.backend.UdvCorpSocialBackend.mapper;

import ru.backend.UdvCorpSocialBackend.dto.comment.CommentResponseDTO;
import ru.backend.UdvCorpSocialBackend.dto.employee.EmployeeDTO;
import ru.backend.UdvCorpSocialBackend.model.Comment;
import ru.backend.UdvCorpSocialBackend.model.Employee;

public class CommentMapper {

    public static CommentResponseDTO toResponseDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setPostId(Long.valueOf(comment.getPost().getId()));
        dto.setEmployee(toEmployeeDTO(comment.getEmployee()));
        dto.setContent(comment.getContent());
        dto.setTimestamp(comment.getTimestamp());
        return dto;
    }

    public static EmployeeDTO toEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setPhotoUrl(employee.getPhotoUrl());
        return dto;
    }
}