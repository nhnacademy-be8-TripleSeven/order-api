package com.tripleseven.orderapi.dto.deliverycode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class DeliveryCodeResponseDTO {
    @JsonProperty("Company")
    private List<DeliveryCodeDTO> Companies;
}
