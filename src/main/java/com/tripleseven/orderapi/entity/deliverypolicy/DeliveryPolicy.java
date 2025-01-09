package com.tripleseven.orderapi.entity.deliverypolicy;

import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class DeliveryPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @OneToMany(mappedBy = "deliveryPolicy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DefaultDeliveryPolicy> deliveryPolicies = new ArrayList<>();

    public void ofCreate(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void ofUpdate(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void addDefaultDeliveryPolicy(DefaultDeliveryPolicy policy) {
        this.deliveryPolicies.add(policy);
    }
}
