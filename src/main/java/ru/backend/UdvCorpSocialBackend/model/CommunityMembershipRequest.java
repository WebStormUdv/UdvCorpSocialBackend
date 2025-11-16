package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Community_Membership_Requests")
public class CommunityMembershipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @CreationTimestamp
    @Column(name = "request_timestamp")
    private LocalDateTime requestTimestamp;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Employee approver;

    @Column(name = "approval_timestamp")
    private LocalDateTime approvalTimestamp;

}
