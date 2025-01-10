package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoUpdateRequestDTO;
import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.exception.notfound.DeliveryInfoNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.deliveryinfo.querydsl.QueryDslDeliveryInfoRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final QueryDslDeliveryInfoRepository queryDslDeliveryInfoRepository;


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
        deliveryInfo.ofCreate(optionalOrderGroup.get());
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        return DeliveryInfoResponseDTO.fromEntity(savedDeliveryInfo);
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO updateDeliveryInfo(Long id, DeliveryInfoUpdateRequestDTO deliveryInfoUpdateRequestDTO) {
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new DeliveryInfoNotFoundException(id);
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.ofUpdate(
                deliveryInfoUpdateRequestDTO.getName(),
                deliveryInfoUpdateRequestDTO.getInvoiceNumber(),
                deliveryInfoUpdateRequestDTO.getArrivedAt());
        return DeliveryInfoResponseDTO.fromEntity(deliveryInfo);
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO updateDeliveryInfoShippingAt(Long id){
        Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);
        if (optionalDeliveryInfo.isEmpty()) {
            throw new DeliveryInfoNotFoundException(id);
        }
        DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
        deliveryInfo.ofShippingUpdate(LocalDate.now());

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

    // front 용
    @Override
    @Transactional(readOnly = true)
    public DeliveryInfoDTO getDeliveryInfoDTO(Long orderGroupId) {
        return queryDslDeliveryInfoRepository.getDeliveryInfo(orderGroupId);
    }
}
