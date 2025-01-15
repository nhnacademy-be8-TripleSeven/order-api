package com.tripleseven.orderapi.dto.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApiProperties {
    @Value("${api.tracking.key}")
    private String trackingKey;

    @Value("${payment.toss.test_widget_api_key}")
    private String widgetApiKey;

    @Value("${payment.toss.test_secret_api_key}")
    private String secretApiKey;
}
