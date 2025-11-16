package ru.backend.UdvCorpSocialBackend.dto.gratitude;

import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.GaType;

import java.time.LocalDateTime;

@Getter
@Setter
public class GratitudeAchievementDTO {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private Integer receiverId;
    private String receiverName;
    private GaType type;
    private String content;
    private String cardUrl;
    private LocalDateTime timestamp;
}