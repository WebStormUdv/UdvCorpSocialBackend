package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.backend.UdvCorpSocialBackend.model.enums.CommunityRole;


@Setter
@Getter
@Entity
@Table(name = "Community_Members")
public class CommunityMember {
    @EmbeddedId
    private CommunityMemberId id;

    @ManyToOne
    @MapsId("communityId")
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private CommunityRole role;

}
