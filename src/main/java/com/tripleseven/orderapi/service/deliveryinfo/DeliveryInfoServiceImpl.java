package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoUpdateRequestDTO;
import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.deliveryinfo.querydsl.QueryDslDeliveryInfoRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final QueryDslDeliveryInfoRepository queryDslDeliveryInfoRepository;


    @Override
    @Transactional(readOnly = true)
    public DeliveryInfoResponseDTO getDeliveryInfoById(Long id) {
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        return DeliveryInfoResponseDTO.fromEntity(deliveryInfo);
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO createDeliveryInfo(DeliveryInfoCreateRequestDTO deliveryInfoCreateRequestDTO) {
        OrderGroup orderGroup = orderGroupRepository.findById(deliveryInfoCreateRequestDTO.getOrderGroupId())
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));


        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(orderGroup);

        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        return DeliveryInfoResponseDTO.fromEntity(savedDeliveryInfo);
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO updateDeliveryInfo(Long id, DeliveryInfoUpdateRequestDTO deliveryInfoUpdateRequestDTO) {
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        deliveryInfo.ofUpdate(
                deliveryInfoUpdateRequestDTO.getName(),
                deliveryInfoUpdateRequestDTO.getInvoiceNumber(),
                deliveryInfoUpdateRequestDTO.getArrivedAt());

        return DeliveryInfoResponseDTO.fromEntity(deliveryInfo);
    }

    @Override
    @Transactional
    public DeliveryInfoResponseDTO updateDeliveryInfoShippingAt(Long id) {
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        deliveryInfo.ofShippingUpdate(LocalDate.now());

        return DeliveryInfoResponseDTO.fromEntity(deliveryInfo);
    }

    @Override
    @Transactional
    public void deleteDeliveryInfo(Long id) {
        if (!deliveryInfoRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
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
