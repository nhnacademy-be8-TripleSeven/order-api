package com.tripleseven.orderapi.dto.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PayCancelRequestDTO {
    @JsonProperty
    private String cancelReason;
    @JsonProperty
    private Long cancelAmount;

    public PayCancelRequestDTO(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
