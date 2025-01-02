package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.exception.notfound.DeliveryInfoNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final OrderGroupRepository orderGroupRepository;

    @Override
    @Transactional(readOnly = true)
    public DeliveryInfoResponseDTO getDeliveryInfoById(Long id) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new DeliveryInfoNotFoundException(id);
        }

        return DeliveryInfoResponseDTO.fromEntity(optionalDeliveryInfo.get());
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO createDeliveryInfo(DeliveryInfoCreateRequestDTO deliveryInfoCreateRequestDTO) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(deliveryInfoCreateRequestDTO.getOrderGroupId());

        if (optionalOrderGroup.isEmpty()) {
            throw new OrderGroupNotFoundException(deliveryInfoCreateRequestDTO.getOrderGroupId());
        }

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(deliveryInfoCreateRequestDTO.getName(), deliveryInfoCreateRequestDTO.getInvoiceNumber(), optionalOrderGroup.get());
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        return DeliveryInfoResponseDTO.fromEntity(savedDeliveryInfo);
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO updateDeliveryInfoArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequestDTO deliveryInfoArrivedAtUpdateRequestDTO) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new DeliveryInfoNotFoundException(id);
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.ofUpdateArrived(deliveryInfoArrivedAtUpdateRequestDTO.getArrivedAt());
        return DeliveryInfoResponseDTO.fromEntity(deliveryInfo);
    }

    @Override
    @Transactional
    public void deleteDeliveryInfo(Long id) {
        if (!deliveryInfoRepository.existsById(id)) {
            throw new DeliveryInfoNotFoundException(id);
        }
        deliveryInfoRepository.deleteById(id);
    }
}
