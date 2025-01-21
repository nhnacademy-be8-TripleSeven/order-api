package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.order.*;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.ordergrouppointhistory.OrderGroupPointHistoryService;
import com.tripleseven.orderapi.service.pay.PayService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDetailService orderDetailService;
    private final OrderGroupService orderGroupService;
    private final WrappingService wrappingService;
    private final OrderGroupPointHistoryService orderGroupPointHistoryService;
    private final DeliveryInfoService deliveryInfoService;
    private final BookCouponApiClient bookCouponApiClient;
    private final PayService payService;

    @Override
    @Transactional(readOnly = true)
    public OrderPayDetailDTO getOrderPayDetail(Long userId, Long orderGroupId) {
        OrderGroupResponseDTO response = orderGroupService.getOrderGroupById(orderGroupId);

        if (!response.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return new OrderPayDetailDTO(
                this.getOrderInfos(orderGroupId),
                this.getOrderGroupInfo(orderGroupId),
                this.getDeliveryInfo(orderGroupId),
                this.getOrderPayInfo(orderGroupId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPayDetailDTO getOrderPayDetailAdmin(Long orderGroupId) {
        return new OrderPayDetailDTO(
                this.getOrderInfos(orderGroupId),
                this.getOrderGroupInfo(orderGroupId),
                this.getDeliveryInfo(orderGroupId),
                this.getOrderPayInfo(orderGroupId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Long getThreeMonthsNetAmount(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);
        Long netAmount = orderDetailService.getNetTotalByPeriod(userId, threeMonthsAgo, today);
        return netAmount;
    }


    private List<OrderInfoDTO> getOrderInfos(Long orderGroupId) {
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        List<OrderInfoDTO> orderInfoList = new ArrayList<>();
        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {

            String bookName = bookCouponApiClient.getBookName(orderDetailResponseDTO.getBookId());

            OrderInfoDTO orderInfoDTO = new OrderInfoDTO(
                    orderDetailResponseDTO.getId(),
                    orderDetailResponseDTO.getOrderStatus(),
                    bookName,
                    orderDetailResponseDTO.getQuantity(),
                    orderDetailResponseDTO.getDiscountPrice(),
                    orderDetailResponseDTO.getPrimePrice());

            orderInfoList.add(orderInfoDTO);
        }
        return orderInfoList;
    }

    private OrderGroupInfoDTO getOrderGroupInfo(Long orderGroupId) {
        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.getOrderGroupById(orderGroupId);
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        long usedPoint = orderGroupPointHistoryService.getUsedPoint(orderGroupId);
        long earnedPoint = orderGroupPointHistoryService.getEarnedPoint(orderGroupId);
        // 판매가 총합
        long primeTotalPrice = 0;
        // 할인 금액
        long discountPrice = 0;

        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {
            primeTotalPrice += orderDetailResponseDTO.getPrimePrice() * orderDetailResponseDTO.getQuantity();
            discountPrice += orderDetailResponseDTO.getDiscountPrice();
        }

        long totalPrice;
        Long wrappingId = orderGroupResponseDTO.getWrappingId();
        if(Objects.isNull(wrappingId)) {
            totalPrice = primeTotalPrice - discountPrice + orderGroupResponseDTO.getDeliveryPrice();
            return new OrderGroupInfoDTO(
                    primeTotalPrice,
                    discountPrice,
                    orderGroupResponseDTO.getDeliveryPrice(),
                    "포장안함",
                    0,
                    totalPrice,
                    usedPoint,
                    earnedPoint);
        }
        WrappingResponseDTO wrappingResponseDTO = wrappingService.getWrappingById(orderGroupResponseDTO.getWrappingId());
        // 총 계산된 금액
        totalPrice = primeTotalPrice - discountPrice + wrappingResponseDTO.getPrice() + orderGroupResponseDTO.getDeliveryPrice();

        return new OrderGroupInfoDTO(
                primeTotalPrice,
                discountPrice,
                orderGroupResponseDTO.getDeliveryPrice(),
                wrappingResponseDTO.getName(),
                wrappingResponseDTO.getPrice(),
                totalPrice,
                usedPoint,
                earnedPoint);
    }

    private DeliveryInfoDTO getDeliveryInfo(Long orderGroupId) {
        return deliveryInfoService.getDeliveryInfoDTO(orderGroupId);
    }

    private OrderPayInfoDTO getOrderPayInfo(Long orderGroupId) {
        return payService.getOrderPayInfo(orderGroupId);
    }


}
