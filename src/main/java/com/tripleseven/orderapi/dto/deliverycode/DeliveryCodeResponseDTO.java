package com.tripleseven.orderapi.dto.deliverycode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCodeResponseDTO {
    @JsonProperty("Company")
    private List<DeliveryCodeDTO> companies;
}
