package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Gratitude_Achievements")
public class GratitudeAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ga_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Employee sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Employee receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GaType type;

    @Column(name = "content")
    private String content;

    @Column(name = "card_url")
    private String cardUrl;

    @CreationTimestamp
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}
