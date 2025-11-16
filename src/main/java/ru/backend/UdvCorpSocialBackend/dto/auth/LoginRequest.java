package ru.backend.UdvCorpSocialBackend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Schema(example = "admin@mail.ru")
    private String email;

    @Schema(example = "SecurePass123!")
    private String password;
}
