package com.tripleseven.orderapi.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class CombinedMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private Object object1;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private Object object2;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private Object object3;

    public CombinedMessageDTO(Object object1, Object object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    public CombinedMessageDTO(Object object1, Object object2, Object object3) {
        this.object1 = object1;
        this.object2 = object2;
        this.object3 = object3;
    }
}
