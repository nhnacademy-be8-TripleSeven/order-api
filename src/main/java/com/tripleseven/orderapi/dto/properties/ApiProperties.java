package com.tripleseven.orderapi.dto.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Getter
//@Profile({"instance1", "instance2"})
public class ApiProperties {
    @Value("${api.tracking.key}")
    private String trackingKey;

    @Value("${payment.toss.test_widget_api_key}")
    private String WIDGET_SECRET_KEY;
    @Value("${payment.toss.test_secret_api_key}")
    private String API_SECRET_KEY;
}
