package com.tripleseven.orderapi.entity.defaultpointpolicy;

import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class DefaultPointPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PointPolicyType pointPolicyType;

    @ManyToOne
    @JoinColumn(name = "point_policy_id", nullable = false)
    private PointPolicy pointPolicy;

    public void ofCreate(PointPolicyType pointPolicyType, PointPolicy pointPolicy) {
        this.pointPolicyType = pointPolicyType;
        this.pointPolicy = pointPolicy;

        pointPolicy.addDefaultPointPolicy(this);
    }

    public void ofUpdate(PointPolicy pointPolicy) {
        this.pointPolicy = pointPolicy;
    }
}