package com.tripleseven.orderapi.business.policy;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpolicy.DefaultPolicyDTO;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultPolicyServiceImpl implements DefaultPolicyService {
    private final DefaultDeliveryPolicyService defaultDeliveryPolicyService;
    private final DefaultPointPolicyService defaultPointPolicyService;

    @Override
    public DefaultPolicyDTO getDefaultPolicies() {
        List<DefaultDeliveryPolicyDTO> defaultDelivery = defaultDeliveryPolicyService.getDefaultDeliveryDTO();
        List<DefaultPointPolicyDTO> defaultPointPolicies = defaultPointPolicyService.getDefaultPointPolicies();

        return new DefaultPolicyDTO(defaultPointPolicies, defaultDelivery);
    }
}
