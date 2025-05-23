package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.backend.UdvCorpSocialBackend.dto.PostCreateDto;
import ru.backend.UdvCorpSocialBackend.dto.PostDto;
import ru.backend.UdvCorpSocialBackend.model.enums.PostType;
import ru.backend.UdvCorpSocialBackend.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PagedResourcesAssembler<PostDto> pagedResourcesAssembler;

    @Autowired
    public PostController(PostService postService, PagedResourcesAssembler<PostDto> pagedResourcesAssembler) {
        this.postService = postService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать новый пост",
            description = "Создает новый глобальный пост для аутентифицированного пользователя. Поддерживает загрузку изображения (JPEG, PNG, до 10 MB)."
    )
    public ResponseEntity<PostDto> createPost(
            @Valid @ModelAttribute PostCreateDto postCreateDto
    ) {
        PostDto postDto = postService.createPost(postCreateDto);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(postDto.getId()).toUri()
        ).body(postDto);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить список постов",
            description = "Возвращает список глобальных постов с фильтрацией по типу и сортировкой."
    )
    public ResponseEntity<Page<PostDto>> getPosts(
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostDto> posts = postService.getPosts(type, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить пост по ID",
            description = "Возвращает конкретный глобальный пост по его ID."
    )
    public ResponseEntity<PostDto> getPostById(@PathVariable Integer id) {
        PostDto postDto = postService.getPostById(id);
        return ResponseEntity.ok(postDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Обновить пост",
            description = "Обновляет глобальный пост по его ID. Доступно только автору поста. Поддерживает загрузку нового изображения (JPEG, PNG, до 10 MB)."
    )
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Integer id,
            @Valid @ModelAttribute PostCreateDto postCreateDto
    ) {
        PostDto postDto = postService.updatePost(id, postCreateDto);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Удалить пост",
            description = "Удаляет глобальный пост по его ID. Доступно только автору или админу."
    )
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить посты сотрудника",
            description = "Возвращает список глобальных постов указанного сотрудника с фильтрацией по типу и сортировкой."
    )
    public ResponseEntity<PagedModel<EntityModel<PostDto>>> getEmployeePosts(
            @PathVariable Integer employeeId,
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostDto> posts = postService.getPostsByEmployeeId(employeeId, type, sortBy, sortDirection, page, size);
        PagedModel<EntityModel<PostDto>> pagedModel = pagedResourcesAssembler.toModel(posts);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/employees/mine")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить свои посты",
            description = "Возвращает список глобальных постов текущего пользователя с фильтрацией по типу и сортировкой."
    )
    public ResponseEntity<PagedModel<EntityModel<PostDto>>> getMyPosts(
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostDto> posts = postService.getPostsByEmployeeId(null, type, sortBy, sortDirection, page, size);
        PagedModel<EntityModel<PostDto>> pagedModel = pagedResourcesAssembler.toModel(posts);
        return ResponseEntity.ok(pagedModel);
    }
}