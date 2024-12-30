package com.tripleseven.orderapi.service.pay;

import com.tripleseven.orderapi.dto.pay.OrderInfoDTO;
import com.tripleseven.orderapi.dto.pay.OrderInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.OrderInfoResponseDTO;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;
    private final OrderGroupService orderGroupService;
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void save(Long userId, JSONObject jsonObject) {
        Pay pay = new Pay();
        OrderInfoDTO infoDto = (OrderInfoDTO) redisTemplate.opsForHash().get(userId.toString(),"OrderInfo");
        //infoDTO를 각 db에 저장해야함

        pay.ofCreate(jsonObject);
        payRepository.save(pay);
    }

    @Override
    public void payCancel(JSONObject response) {
        Pay pay = payRepository.findByPaymentKey(response.get("paymentKey").toString());
        if (Objects.isNull(pay)) {
            throw new RuntimeException();
        }
        pay.ofUpdate(response);
        payRepository.save(pay);
    }

    @Override
    public OrderInfoResponseDTO getOrderInfo(Long userId, OrderInfoRequestDTO requestDTO) {
        long orderId = UUID.randomUUID().getMostSignificantBits();
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.ofCreate(orderId,requestDTO);
        redisTemplate.opsForHash().put(userId.toString(),"OrderInfo",orderInfoDTO);
        return new OrderInfoResponseDTO(orderId, requestDTO.getTotalAmount());
    }
}
