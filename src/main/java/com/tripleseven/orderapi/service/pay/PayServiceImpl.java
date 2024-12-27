package com.tripleseven.orderapi.service.pay;

import com.tripleseven.orderapi.dto.pay.OrderInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.OrderInfoResponseDTO;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;

    private final RedisTemplate<String, Object>redisTemplate;


    @Override
    public void save(JSONObject jsonObject) {
        Pay pay = new Pay();
        pay.ofCreate(jsonObject);
        payRepository.save(pay);
    }

    @Override
    public void payCancel(JSONObject response) {
        Pay pay = payRepository.findByPaymentKey(response.get("paymentKey").toString());
        if(Objects.isNull(pay)){
            throw new RuntimeException();
        }
        pay.ofUpdate(response);
        payRepository.save(pay);
    }

    @Override
    public OrderInfoResponseDTO getOrderInfo(OrderInfoRequestDTO requestDTO) {
        long orderId = UUID.randomUUID().getLeastSignificantBits();
        // 주문자 정보 맵에 담기
        Map<String, String> orderInfo = new HashMap<>();
        orderInfo.put("customerName", requestDTO.getCustomerName());
        orderInfo.put("customerPhone", requestDTO.getCustomerPhone());
        orderInfo.put("customerLandline", requestDTO.getCustomerLandline());
        orderInfo.put("customerEmail", requestDTO.getCustomerEmail());
        orderInfo.put("customerPassword", requestDTO.getCustomerPassword());

        // 받는 사람 정보 추가
        orderInfo.put("recipientName", requestDTO.getRecipientName());
        orderInfo.put("recipientPhone", requestDTO.getRecipientPhone());
        orderInfo.put("recipientLandline", requestDTO.getRecipientLandline());
        orderInfo.put("recipientAddress", requestDTO.getRecipientAddress());
        log.debug("before : {}",orderInfo);
        redisTemplate.opsForHash().putAll("order:" + orderId,orderInfo);
        log.debug("after : {}",redisTemplate.opsForHash().get("order:",orderId));
        return new OrderInfoResponseDTO(orderId, requestDTO.getTotalAmount());
    }
}
