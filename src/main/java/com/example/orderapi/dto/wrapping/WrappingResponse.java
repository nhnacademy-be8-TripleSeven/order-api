package com.example.orderapi.dto.wrapping;

import com.example.orderapi.entity.wrapping.Wrapping;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public class WrappingResponse {
    private final Long id;

    private final String name;

    private final int price;

    @Builder
    private WrappingResponse(Long id, String name, int price) {
        if (Objects.isNull(id)) {
            log.error("Wrapping id cannot be null");
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static WrappingResponse fromEntity(Wrapping wrapping) {
        return WrappingResponse.builder()
                .id(wrapping.getId())
                .name(wrapping.getName())
                .price(wrapping.getPrice())
                .build();
    }
}
