package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.backend.UdvCorpSocialBackend.dto.comment.CommentRequestDTO;
import ru.backend.UdvCorpSocialBackend.dto.comment.CommentResponseDTO;
import ru.backend.UdvCorpSocialBackend.mapper.CommentMapper;
import ru.backend.UdvCorpSocialBackend.model.Comment;
import ru.backend.UdvCorpSocialBackend.service.CommentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Контроллер для работы с комментариями к постам")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Добавить комментарий к посту",
            description = "Добавляет комментарий от текущего пользователя к указанному посту."
    )
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Integer postId,
            @Valid @RequestBody CommentRequestDTO requestDTO) {
        Comment comment = commentService.addComment(postId, requestDTO.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentMapper.toResponseDTO(comment));
    }

    @GetMapping
    @Operation(
            summary = "Получить комментарии к посту",
            description = "Возвращает постраничный список комментариев для указанного поста."
    )
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByPost(
            @PathVariable Integer postId,
            @PageableDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            if (property.isEmpty() || (!property.equals("id") && !property.equals("timestamp") && !property.equals("content"))) {
                throw new IllegalArgumentException("Invalid sort property: " + property + ". Allowed: id, timestamp, content");
            }
        }
        Page<Comment> comments = commentService.getCommentsByPostId(postId, pageable);
        Page<CommentResponseDTO> response = comments.map(CommentMapper::toResponseDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Обновить комментарий",
            description = "Обновляет содержимое указанного комментария, если пользователь является его автором."
    )
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDTO requestDTO) {
        Comment updatedComment = commentService.updateComment(commentId, requestDTO.getContent());
        return ResponseEntity.ok(CommentMapper.toResponseDTO(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет указанный комментарий, если пользователь является его автором."
    )
    public ResponseEntity<Void> removeComment(@PathVariable Long commentId) {
        commentService.removeComment(commentId);
        return ResponseEntity.noContent().build();
    }
}