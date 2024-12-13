package com.example.orderapi.service.deliveryinfo;

import com.example.orderapi.dto.deliveryinfo.*;
import com.example.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.example.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    private final DeliveryInfoRepository deliveryInfoRepository;


    @Override
    public DeliveryInfoResponse getById(Long id) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }

        return DeliveryInfoResponse.fromEntity(optionalDeliveryInfo.get());
    }

    @Override
    public DeliveryInfoResponse create(DeliveryInfoCreateRequest deliveryInfoCreateRequest) {
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setName(deliveryInfoCreateRequest.getName());
        deliveryInfo.setInvoiceNumber(deliveryInfoCreateRequest.getInvoiceNumber());
        deliveryInfoRepository.save(deliveryInfo);
        return DeliveryInfoResponse.fromEntity(deliveryInfoRepository.save(deliveryInfo));
    }

    @Override
    public DeliveryInfoResponse updateAll(Long id, DeliveryInfoUpdateRequest deliveryInfoUpdateRequest) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.setDeliveryDate(deliveryInfoUpdateRequest.getDeliveryDate());
        deliveryInfo.setForwardedAt(deliveryInfoUpdateRequest.getForwardedAt());
        deliveryInfo.setArrivedAt(deliveryInfoUpdateRequest.getArrivedAt());
        DeliveryInfo updateDeliverInfo = deliveryInfoRepository.save(deliveryInfo);
        return DeliveryInfoResponse.fromEntity(updateDeliverInfo);
    }

    @Override
    public DeliveryInfoResponse updateLogistics(Long id, DeliveryInfoLogisticsUpdateRequest deliveryInfoLogisticsUpdateRequest) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.setForwardedAt(deliveryInfoLogisticsUpdateRequest.getForwardedAt());
        deliveryInfo.setDeliveryDate(deliveryInfoLogisticsUpdateRequest.getDeliveryDate());
        DeliveryInfo updateDeliverInfo = deliveryInfoRepository.save(deliveryInfo);
        return DeliveryInfoResponse.fromEntity(updateDeliverInfo);
    }

    @Override
    public DeliveryInfoResponse updateArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequest deliveryInfoArrivedAtUpdateRequest) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.setArrivedAt(deliveryInfoArrivedAtUpdateRequest.getArrivedAt());
        return DeliveryInfoResponse.fromEntity(deliveryInfo);
    }

    @Override
    public void delete(Long id) {
        deliveryInfoRepository.deleteById(id);
    }
}
