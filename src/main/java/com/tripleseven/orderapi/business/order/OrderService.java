package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.dto.order.OrderPayDetailDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    OrderPayDetailDTO getOrderPayDetail(Long userId, Long orderGroupId);

    OrderPayDetailDTO getOrderPayDetailAdmin(Long orderGroupId);

    Long getThreeMonthsNetAmount(Long userId);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Long saveOrderInfo(Long userId, PayInfoDTO payInfo, PaymentDTO payment, OrderGroupCreateRequestDTO request);
}
