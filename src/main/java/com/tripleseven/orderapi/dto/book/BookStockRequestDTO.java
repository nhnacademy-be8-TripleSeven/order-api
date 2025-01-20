package com.tripleseven.orderapi.dto.book;

import lombok.Value;

@Value
public class BookStockRequestDTO {
    private Long bookId;
    private Integer stockToReduce;
}
