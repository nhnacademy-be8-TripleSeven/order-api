package com.tripleseven.orderapi.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.*;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.point.PointDTO;

import java.util.HashMap;
import java.util.Map;


public class CombinedMessageDTO {
    @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CartItemDTO.class, name = "CartItemDTO"),
            @JsonSubTypes.Type(value = PointDTO.class, name = "PointDTO"),
            @JsonSubTypes.Type(value = OrderGroupCreateRequestDTO.class, name = "OrderGroupCreateRequestDTO")
    })
    private final Map<String, Object> objectMap = new HashMap<>();

    public void addObject(String key, Object object) {
        this.objectMap.put(key, object);
    }

    public Object getObject(String key) {
        return this.objectMap.get(key);
    }
}