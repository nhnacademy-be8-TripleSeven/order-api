package com.tripleseven.orderapi.dto.pay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayCancelRequestDTO {
    @JsonProperty
    private String cancelReason;
    @JsonProperty
    private Long cancelAmount;
}
