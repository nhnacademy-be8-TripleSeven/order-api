package com.tripleseven.orderapi.dto;

import lombok.Getter;

import java.util.*;

@Getter
public class WrappingMessageObject {
    private List<Object> objectList = new ArrayList<>();

    public List<Object> addWrappingObjects(Object... objects){
        return Collections.singletonList(objectList.addAll(Arrays.asList(objects)));
    }
}
