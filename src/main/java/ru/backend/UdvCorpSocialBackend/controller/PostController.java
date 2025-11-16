package ru.backend.UdvCorpSocialBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import ru.backend.UdvCorpSocialBackend.dto.post.*;
import ru.backend.UdvCorpSocialBackend.model.PostType;
import ru.backend.UdvCorpSocialBackend.service.PostService;

@RestController
@RequestMapping("/api")
@Tag(name = "Posts", description = "Контроллер для работы с постами")
public class PostController {

    private final PostService postService;
    private final PagedResourcesAssembler<PostDto> pagedResourcesAssembler;

    @Autowired
    public PostController(PostService postService, PagedResourcesAssembler<PostDto> pagedResourcesAssembler) {
        this.postService = postService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать глобальный пост",
            description = "Создает новый глобальный пост для аутентифицированного пользователя. Поддерживает загрузку изображения (JPEG, PNG, до 10 MB)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пост успешно создан", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста", content = @Content),
            @ApiResponse(responseCode = "403", description = "Достигнут дневной лимит постов", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    public ResponseEntity<PostDto> createGlobalPost(
            @Valid @ModelAttribute PostCreateDto postCreateDto
    ) {
        PostDto postDto = postService.createPost(postCreateDto);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(postDto.getId()).toUri()
        ).body(postDto);
    }

    @PostMapping(value = "/communities/{communityId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать пост в сообществе",
            description = "Создает новый пост в указанном сообществе. Пользователь должен быть участником сообщества, а для закрытых сообществ — администратором."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пост успешно создан", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста", content = @Content),
            @ApiResponse(responseCode = "403", description = "Пользователь не является участником или не имеет прав", content = @Content),
            @ApiResponse(responseCode = "404", description = "Сообщество или пользователь не найдены", content = @Content)
    })
    public ResponseEntity<PostDto> createCommunityPost(
            @PathVariable Integer communityId,
            @Valid @ModelAttribute PostCreateDto postCreateDto
    ) {
        postCreateDto.setCommunityId(communityId);
        PostDto postDto = postService.createPost(postCreateDto);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(postDto.getId()).toUri()
        ).body(postDto);
    }

    @GetMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить список глобальных постов",
            description = "Возвращает список глобальных постов с фильтрацией по типу и сортировкой."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список постов успешно получен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content)
    })
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

    @GetMapping("/communities/{communityId}/posts")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить посты сообщества",
            description = "Возвращает список постов в указанном сообществе с фильтрацией по типу и сортировкой."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список постов успешно получен", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ к постам закрытого сообщества запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Сообщество не найдено", content = @Content)
    })
    public ResponseEntity<PagedModel<EntityModel<PostDto>>> getCommunityPosts(
            @PathVariable Integer communityId,
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostDto> posts = postService.getCommunityPosts(communityId, type, sortBy, sortDirection, page, size);
        PagedModel<EntityModel<PostDto>> pagedModel = pagedResourcesAssembler.toModel(posts);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить пост по ID",
            description = "Возвращает конкретный пост по его ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост успешно получен", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ к посту закрытого сообщества запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пост не найден", content = @Content)
    })
    public ResponseEntity<PostDto> getPostById(@PathVariable Integer id) {
        PostDto postDto = postService.getPostById(id);
        return ResponseEntity.ok(postDto);
    }

    @PutMapping(value = "/posts/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Обновить пост",
            description = "Обновляет пост по его ID. Доступно только автору поста."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост успешно обновлен", content = @Content),
            @ApiResponse(responseCode = "403", description = "Только автор может обновлять пост", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пост не найден", content = @Content)
    })
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Integer id,
            @Valid @ModelAttribute PostCreateDto postCreateDto
    ) {
        PostDto postDto = postService.updatePost(id, postCreateDto);
        return ResponseEntity.ok(postDto);
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Удалить пост",
            description = "Удаляет пост по его ID. Доступно только автору, админу или администратору сообщества."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пост успешно удален", content = @Content),
            @ApiResponse(responseCode = "403", description = "Только автор, админ или администратор сообщества могут удалять пост", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пост не найден", content = @Content)
    })
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees/{employeeId}/posts")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить посты сотрудника",
            description = "Возвращает список глобальных постов указанного сотрудника с фильтрацией по типу и сортировкой."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список постов успешно получен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content)
    })
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

    @GetMapping("/employees/mine/posts")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить свои посты",
            description = "Возвращает список глобальных постов текущего пользователя с фильтрацией по типу и сортировкой."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список постов успешно получен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
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