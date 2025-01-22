package com.tripleseven.orderapi.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.HashMap;
import java.util.Map;


public class CombinedMessageDTO {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Long.class, name = "Long"),
            @JsonSubTypes.Type(value = Object.class, name = "Object")
    })
    private final Map<String, Object> objectMap = new HashMap<>();

    public void addObject(String key, Object object) {
        this.objectMap.put(key, object);
    }

    public Object getObject(String key) {
        return this.objectMap.get(key);
    }
}