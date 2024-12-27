package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoResponseDTO {
    private Long orderId;
    private Long totalAmount;
}
