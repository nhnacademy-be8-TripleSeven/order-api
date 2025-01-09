package com.tripleseven.orderapi.entity.defaultdeliverypolicy;

import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class DefaultDeliveryPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private DeliveryPolicyType deliveryPolicyType;

    @ManyToOne
    @JoinColumn(name = "delivery_policy_id", nullable = false)
    private DeliveryPolicy deliveryPolicy;

    public void ofCreate(DeliveryPolicyType deliveryPolicyType, DeliveryPolicy deliveryPolicy) {
        this.deliveryPolicyType = deliveryPolicyType;
        this.deliveryPolicy = deliveryPolicy;

        deliveryPolicy.addDefaultDeliveryPolicy(this);
    }

    public void ofUpdate(DeliveryPolicy deliveryPolicy) {
        this.deliveryPolicy = deliveryPolicy;
    }

}
