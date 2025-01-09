package com.tripleseven.orderapi.dto.defaultpolicy;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DefaultPolicyDTO {
    List<DefaultPointPolicyDTO> pointPolicies;
    List<DefaultDeliveryPolicyDTO> deliveryPolicies;
}
