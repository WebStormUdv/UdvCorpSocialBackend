package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ru.backend.UdvCorpSocialBackend.service.LikeService;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Likes", description = "Апи для работы с лайками")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    private LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{id}/likes")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Поставить лайк посту",
            description = "Добавляет лайк от текущего пользователя к указанному посту."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Лайк успешно поставлен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пост или пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "409", description = "Вы уже лайкнули этот пост", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён или лайк уже поставлен", content = @Content)
    })
    public ResponseEntity<Void> addLike(@PathVariable Integer id) {
        likeService.addLike(id);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{id}/likes")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Убрать лайк с поста",
            description = "Удаляет лайк текущего пользователя с указанного поста."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Лайк успешно убран", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пост или лайк не найден", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content)
    })
    public ResponseEntity<Void> removeLike(@PathVariable Integer id) {
        likeService.removeLike(id);
        return ResponseEntity.noContent().build();
    }
}
