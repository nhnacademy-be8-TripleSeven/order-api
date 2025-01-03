package com.tripleseven.orderapi.service.pay;

import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
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

    // TODO save 해서 반환받아야할 경우가 있는지 확인
    //  저장되는 값 더 추가해야됨
    @Override
    public void save(Long userId, JSONObject jsonObject) {
        Pay pay = new Pay();
        PayInfoDTO infoDto = (PayInfoDTO) redisTemplate.opsForHash().get(userId.toString(), "OrderInfo");
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
    public PayInfoResponseDTO getOrderInfo(Long userId, PayInfoRequestDTO requestDTO) {
        long orderId = UUID.randomUUID().getMostSignificantBits();
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.ofCreate(orderId, requestDTO);
        // TODO 검증 필요 (ex payCheckService)


        // TODO 검증 후 저장
        redisTemplate.opsForHash().put(userId.toString(), "OrderInfo", payInfoDTO);
        return new PayInfoResponseDTO(orderId, requestDTO.getTotalAmount());
    }

    public OrderPayInfoDTO getOrderPayInfo(Long orderId){

        return payRepository.getDTOByOrderGroupId(orderId);
    }

}
