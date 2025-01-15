package com.tripleseven.orderapi.business.order.process;

import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.dto.CombinedMessageDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pay.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderSaveProcessing implements OrderProcessing {

    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    private static final String CART_ROUTING_KEY = "cart.routing.key";
    private static final String COUPON_ROUTING_KEY = "coupon.routing.key";

    public static final Long GUEST_USER_ID = -1L;


    private final OrderGroupService orderGroupService;
    private final OrderDetailService orderDetailService;
    private final DeliveryInfoService deliveryInfoService;
    private final DefaultDeliveryPolicyService defaultDeliveryPolicyService;
    private final PointService pointService;
    private final PayService payService;

    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void processNonMemberOrder(String guestId) {
        // TODO Redis 저장 키 고민
        log.info("processNonMemberOrder guestId={}", guestId);

        HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

        if (Objects.isNull(payHash)) {
            throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
        }

        PayInfoDTO payInfo = payHash.get(guestId, "PayInfo");

        List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();

        orderProcessing(GUEST_USER_ID, payInfo);

        cartProcessing(guestId, bookInfos);

        log.info("Successfully processed non-member order");
    }

    @Override
    public void processMemberOrder(Long memberId) {
        log.info("processMemberOrder memberId={}", memberId);

        HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

        if (Objects.isNull(payHash)) {
            throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
        }

        PayInfoDTO payInfo = payHash.get(memberId.toString(), "PayInfo");

        List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();


        Long orderGroupId = orderProcessing(memberId, payInfo);

        cartProcessing(memberId.toString(), bookInfos);

        // TODO 결제 정보 저장
        //  orderId랑 같이 저장

        Long point = payInfo.getPoint();
        Long totalAmount = payInfo.getTotalAmount();

        pointProcessing(memberId, orderGroupId, point, totalAmount);

        couponProcessing(memberId, payInfo.getCouponId());

        log.info("Successfully processed member order");
    }

    // 주문 저장
    private Long orderProcessing(Long userId, PayInfoDTO payInfo) {

        DefaultDeliveryPolicyDTO defaultDeliveryPolicy = defaultDeliveryPolicyService.getDefaultDeliveryPolicy(DeliveryPolicyType.DEFAULT);
        AddressInfoDTO addressInfo = payInfo.getAddressInfo();
        RecipientInfoDTO recipientInfo = payInfo.getRecipientInfo();

        String address = String.format("%s %s (%s)",
                addressInfo.getRoadAddress(),
                addressInfo.getDetailAddress(),
                addressInfo.getZoneAddress());

        OrderGroupCreateRequestDTO request = new OrderGroupCreateRequestDTO(
                payInfo.getWrapperId(),
                payInfo.getOrdererName(),
                recipientInfo.getRecipientName(),
                recipientInfo.getRecipientPhone(),
                recipientInfo.getRecipientLandline(),
                defaultDeliveryPolicy.getPrice(),
                address
        );

        // OrderGroup 생성
        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(userId, request);
        Long orderGroupId = orderGroupResponseDTO.getId();


        // DeliveryInfo 생성
        deliveryInfoService.createDeliveryInfo(
                new DeliveryInfoCreateRequestDTO(orderGroupId, payInfo.getDeliveryDate())
        );

        // OrderDetail 저장
        List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();
        for (OrderBookInfoDTO bookInfo : bookInfos) {
            OrderDetailCreateRequestDTO orderDetailCreateRequestDTO
                    = new OrderDetailCreateRequestDTO(
                    bookInfo.getBookId(),
                    bookInfo.getQuantity(),
                    bookInfo.getPrice(),
                    bookInfo.getCouponSalePrice(),
                    orderGroupId
            );
            orderDetailService.createOrderDetail(orderDetailCreateRequestDTO);
        }

        return orderGroupId;
    }

    // 장바구니 초기화
    private void cartProcessing(String userId, List<OrderBookInfoDTO> bookInfos) {
        List<Long> bookIds = bookInfos.stream().map(OrderBookInfoDTO::getBookId).collect(Collectors.toList());

        CombinedMessageDTO cartMessageDTO = new CombinedMessageDTO();
        cartMessageDTO.addObject("BookIds", bookIds);
        cartMessageDTO.addObject("UserId", userId);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CART_ROUTING_KEY, cartMessageDTO);
    }

    // 포인트 사용/적립
    private void pointProcessing(long userId, long orderId, long point, long amount) {
        int usedPoint = (int) point;
        int totalAmount = (int) amount;

        if (usedPoint > 0) {
            pointService.createPointHistoryForPaymentSpend(userId, usedPoint, orderId);
        }

        pointService.createPointHistoryForPaymentEarn(userId, totalAmount, orderId);
    }

    // 쿠폰 사용
    private void couponProcessing(Long memberId, Long couponId) {
        CombinedMessageDTO couponMessageDTO = new CombinedMessageDTO();
        couponMessageDTO.addObject("couponId", couponId);
        couponMessageDTO.addObject("userId", memberId);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, COUPON_ROUTING_KEY, couponMessageDTO);
    }
}
