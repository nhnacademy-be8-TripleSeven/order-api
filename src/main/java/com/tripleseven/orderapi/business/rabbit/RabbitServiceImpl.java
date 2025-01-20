package com.tripleseven.orderapi.business.rabbit;

import com.tripleseven.orderapi.dto.CombinedMessageDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitServiceImpl implements RabbitService {
    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    private static final String CART_ROUTING_KEY = "cart.routing.key";
    private static final String POINT_ROUTING_KEY = "point.routing.key";

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendCartMessage(String userId, List<OrderBookInfoDTO> bookInfos) {
        try {
            List<Long> bookIds = bookInfos.stream().map(OrderBookInfoDTO::getBookId).toList();
            CombinedMessageDTO cartMessageDTO = new CombinedMessageDTO();
            cartMessageDTO.addObject("BookIds", bookIds);
            cartMessageDTO.addObject("UserId", userId);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, CART_ROUTING_KEY, cartMessageDTO);
            log.info("send cart message success");
        } catch (Exception e) {
            log.error("Error sending cart message: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendPointMessage(Long userId, Long orderId, long totalAmount) {
        try {
            CombinedMessageDTO pointMessageDTO = new CombinedMessageDTO();
            pointMessageDTO.addObject("UserId", userId);
            pointMessageDTO.addObject("TotalAmount", totalAmount);
            pointMessageDTO.addObject("OrderId", orderId);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, POINT_ROUTING_KEY, pointMessageDTO);
            log.info("send point message success");
        } catch (Exception e) {
            log.error("Error sending point message: {}", e.getMessage());
        }
    }
}
