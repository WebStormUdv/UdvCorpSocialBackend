package ru.backend.UdvCorpSocialBackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.GaType;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class GratitudeAchievementCreateDTO {
    @NotNull(message = "Receiver ID cannot be null")
    private Integer receiverId;

    @NotNull(message = "Type cannot be null")
    private GaType type;

    @Size(max = 1000, message = "Content must not exceed 1000 characters")
    private String content;

    private MultipartFile card;
}