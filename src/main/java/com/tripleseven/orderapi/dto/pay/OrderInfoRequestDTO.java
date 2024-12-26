package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoRequestDTO {
    private String address;
    private Long usedPoints;
    private Long totalAmount;
}
