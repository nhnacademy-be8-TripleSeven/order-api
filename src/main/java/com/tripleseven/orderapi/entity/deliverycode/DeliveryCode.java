package com.tripleseven.orderapi.entity.deliverycode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
public class DeliveryCode {
    @Id
    private String id;

    private boolean international;

    @NotNull
    @NotBlank
    private String name;

    public void ofCreate(String id, boolean international, String name) {
        this.id = id;
        this.international = international;
        this.name = name;
    }
}
