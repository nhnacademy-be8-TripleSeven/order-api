package com.tripleseven.orderapi.common.listener;

import com.tripleseven.orderapi.service.deliverycode.DeliveryCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"instance1", "instance2"})
public class TrackingDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final DeliveryCodeService deliveryCodeService;

    @Value("${api.tracking.key}")
    private String apiKey;

    @Value("${app.initialize}")
    private boolean initializeData;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (initializeData) {
            String apiUrl = "https://info.sweettracker.co.kr/api/v1/companylist?t_key=" + apiKey;
            deliveryCodeService.saveDeliveryCode(apiUrl);
            log.info("Tracking Data fetched and saved during server startup.");
        }
    }
}
