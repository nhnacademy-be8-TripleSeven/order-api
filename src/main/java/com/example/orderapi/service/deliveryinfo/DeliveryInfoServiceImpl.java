package com.example.orderapi.service.deliveryinfo;

import com.example.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoLogisticsUpdateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.example.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.example.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    private final DeliveryInfoRepository deliveryInfoRepository;

    @Override
    public DeliveryInfoResponse getDeliveryInfoById(Long id) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }

        return DeliveryInfoResponse.fromEntity(optionalDeliveryInfo.get());
    }

    @Override
    public DeliveryInfoResponse createDeliveryInfo(DeliveryInfoCreateRequest deliveryInfoCreateRequest) {
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(deliveryInfoCreateRequest.getName(), deliveryInfoCreateRequest.getInvoiceNumber());
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);
        return DeliveryInfoResponse.fromEntity(savedDeliveryInfo);
    }

    @Override
    public DeliveryInfoResponse updateDeliveryInfoLogistics(Long id, DeliveryInfoLogisticsUpdateRequest deliveryInfoLogisticsUpdateRequest) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.ofUpdateLogistics(deliveryInfoLogisticsUpdateRequest.getForwardedAt(), deliveryInfoLogisticsUpdateRequest.getDeliveryDate());
        return DeliveryInfoResponse.fromEntity(deliveryInfo);
    }

    @Override
    public DeliveryInfoResponse updateDeliveryInfoArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequest deliveryInfoArrivedAtUpdateRequest) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new RuntimeException();
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.ofUpdateArrived(deliveryInfoArrivedAtUpdateRequest.getArrivedAt());
        return DeliveryInfoResponse.fromEntity(deliveryInfo);
    }

    @Override
    public void deleteDeliveryInfo(Long id) {
        if (!deliveryInfoRepository.existsById(id)) {
            throw new RuntimeException();
        }
        deliveryInfoRepository.deleteById(id);
    }
}
