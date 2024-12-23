package com.tripleseven.orderapi.dto.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApiProperties {
    @Value("${api.tracking.key}")
    private String trackingKey;
}
