package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequest;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequest;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final OrderGroupRepository orderGroupRepository;

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
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(deliveryInfoCreateRequest.getId());

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(deliveryInfoCreateRequest.getName(), deliveryInfoCreateRequest.getInvoiceNumber(), optionalOrderGroup.get());
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);
        return DeliveryInfoResponse.fromEntity(savedDeliveryInfo);
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
