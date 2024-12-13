package com.example.orderapi.dto.wrapping;

import com.example.orderapi.entity.Wrapping;
import lombok.Data;

@Data
public class WrappingResponse {
    private Long id;

    private String name;

    private int price;

    public static WrappingResponse fromEntity(Wrapping wrapping) {
        WrappingResponse dto = new WrappingResponse();
        dto.setId(wrapping.getId());
        dto.setName(wrapping.getName());
        dto.setPrice(wrapping.getPrice());
        return dto;
    }
}
