package ru.backend.UdvCorpSocialBackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class CommunityMemberId implements Serializable {
    @Column(name = "community_id")
    private Integer communityId;

    @Column(name = "employee_id")
    private Integer employeeId;

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityMemberId that = (CommunityMemberId) o;
        return communityId.equals(that.communityId) && employeeId.equals(that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(communityId, employeeId);
    }
}
