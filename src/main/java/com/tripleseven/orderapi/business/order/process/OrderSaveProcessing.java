package com.tripleseven.orderapi.business.order.process;

import com.tripleseven.orderapi.business.order.OrderService;
import com.tripleseven.orderapi.business.rabbit.RabbitService;
import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSaveProcessing implements OrderProcessing {
    public static final Long GUEST_USER_ID = -1L;

    private final OrderService orderService;
    private final RabbitService rabbitService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void processOrder(Long memberId, String guestId, PaymentDTO paymentDTO) {
        log.info("processOrder Id={}", memberId != null ? memberId : guestId);

        HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

        if (Objects.isNull(payHash)) {
            throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
        }
        PayInfoDTO payInfo;
        Long orderGroupId;

        if (memberId != null) {
            payInfo = payHash.get(memberId.toString(), "PayInfo");

            if (Objects.isNull(payInfo)) {
                throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
            }

            OrderGroupCreateRequestDTO request = getOrderGroupCreateRequestDTO(payInfo);
            List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();

            orderGroupId = orderService.saveOrderInfo(memberId, payInfo, paymentDTO, request);
            rabbitService.sendCartMessage(memberId, guestId, bookInfos);
            rabbitService.sendPointMessage(memberId, orderGroupId, payInfo.getTotalAmount());

        } else {
            payInfo = payHash.get(guestId, "PayInfo");

            OrderGroupCreateRequestDTO request = getOrderGroupCreateRequestDTO(payInfo);
            List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();

            orderService.saveOrderInfo(GUEST_USER_ID, payInfo, paymentDTO, request);
            rabbitService.sendCartMessage(null, guestId, bookInfos);
        }
    }


    private OrderGroupCreateRequestDTO getOrderGroupCreateRequestDTO(PayInfoDTO payInfo) {
        AddressInfoDTO addressInfo = payInfo.getAddressInfo();
        RecipientInfoDTO recipientInfo = payInfo.getRecipientInfo();

        String address = addressInfo.getZoneAddress() != null ?
                String.format("%s %s (%s)",
                        addressInfo.getRoadAddress().trim(),
                        addressInfo.getDetailAddress().trim(),
                        addressInfo.getZoneAddress().trim()) :
                String.format("%s %s",
                        addressInfo.getRoadAddress().trim(),
                        addressInfo.getDetailAddress().trim());

        return new OrderGroupCreateRequestDTO(
                payInfo.getWrapperId(),
                payInfo.getOrdererName(),
                recipientInfo.getRecipientName(),
                recipientInfo.getRecipientPhone(),
                recipientInfo.getRecipientLandline(),
                payInfo.getDeliveryFee(),
                address
        );
    }
}