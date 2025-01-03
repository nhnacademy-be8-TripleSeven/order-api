package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayInfoResponseDTO {
    private Long orderId;
    private Long totalAmount;
}
