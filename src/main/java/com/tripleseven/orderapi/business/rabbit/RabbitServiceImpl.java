package com.tripleseven.orderapi.business.rabbit;

import com.tripleseven.orderapi.business.feign.MemberService;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitServiceImpl implements RabbitService {
    private final PointService pointService;
    private final MemberService memberService;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendCartMessage(Long userId, String guestId, List<OrderBookInfoDTO> bookInfos) {
        List<Long> bookIds = bookInfos.stream().map(OrderBookInfoDTO::getBookId).toList();
        memberService.clearCart(userId, guestId, bookIds);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendPointMessage(Long userId, Long orderId, long totalAmount) {
        pointService.createPointHistoryForPaymentEarn(userId, totalAmount, orderId);
    }
}
