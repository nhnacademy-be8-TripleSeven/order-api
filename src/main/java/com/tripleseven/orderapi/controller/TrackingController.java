package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.service.deliverycode.DeliveryCodeService;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@RestController
@Profile({"instance1", "instance2"})
public class TrackingController {
    private final DeliveryInfoService deliveryInfoService;
    private final DeliveryCodeService deliveryCodeService;
    private final ApiProperties apiProperties;

    @PostMapping("/api/orders/tracking/{id}")
    public RedirectView showDeliveryInfo(@PathVariable Long id) {
        DeliveryInfoResponseDTO response = deliveryInfoService.getDeliveryInfoById(id);
        String code = deliveryCodeService.getDeliveryCodeToName(response.getName());
        String apiUrl = "https://info.sweettracker.co.kr/tracking/4?" +
                "t_code=" + code +
                "&t_invoice=" + response.getInvoiceNumber() +
                "&t_key=" + apiProperties.getTrackingKey();
        return new RedirectView(apiUrl);
    }
}
