package com.tripleseven.orderapi.service.deliverycode;

public interface DeliveryCodeService {
    void saveDeliveryCode(String url);
    String getDeliveryCodeToName(String name);
}
