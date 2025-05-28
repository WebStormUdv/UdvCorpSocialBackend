package ru.backend.UdvCorpSocialBackend.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class GratitudeAchievementUpdateDTO {
    @Size(max = 1000, message = "Content must not exceed 1000 characters")
    private String content;

    private MultipartFile card;
}