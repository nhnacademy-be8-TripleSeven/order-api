package com.tripleseven.orderapi.common.initializer;

import com.tripleseven.orderapi.service.deliverycode.DeliveryCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final DeliveryCodeService deliveryCodeService;

    @Value("${api.tracking.key}")
    private String apiKey;

    @Value("${app.initialize}")
    private boolean initializeData;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (initializeData) {
            String apiUrl = "https://info.sweettracker.co.kr/api/v1/companylist?t_key="+apiKey;
            deliveryCodeService.saveDeliveryCode(apiUrl);
            System.out.println("Data fetched and saved during server startup.");
        }
    }
}
