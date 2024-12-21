package com.tripleseven.orderapi.dto.pay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayCancelRequest {
    @JsonProperty
    private String cancelReason;
    @JsonProperty
    private Long cancelAmount;
}
