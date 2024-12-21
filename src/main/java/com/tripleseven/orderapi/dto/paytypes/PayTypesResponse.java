package com.tripleseven.orderapi.dto.paytypes;

import com.tripleseven.orderapi.entity.paytypes.PayTypes;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
public class PayTypesResponse {
    private final Long id;
    private final String name;

    @Builder
    private PayTypesResponse(Long id, String name) {
        if(Objects.isNull(id)) {
            throw new IllegalArgumentException("id can not be null");
        }
        if(Objects.isNull(name)) {
            throw new IllegalArgumentException("name can not be null");
        }
        this.id = id;
        this.name = name;
    }
    public static PayTypesResponse fromEntity(PayTypes payTypes){
        return new PayTypesResponse(
                payTypes.getId(),
                payTypes.getName()
        );
    }
}
