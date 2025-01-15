package com.tripleseven.orderapi.dto.deliverycode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class DeliveryCodeDTO {
    @JsonProperty("Code")
    String code;
    @JsonProperty("International")
    boolean international;
    @JsonProperty("Name")
    String name;
}
