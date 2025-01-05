package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.order.*;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.repository.deliveryinfo.querydsl.QueryDslDeliveryInfoRepository;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pay.PayService;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDetailService orderDetailService;
    private final OrderGroupService orderGroupService;
    private final WrappingService wrappingService;
    private final PointHistoryService pointHistoryService;
    private final QueryDslDeliveryInfoRepository queryDslDeliveryInfoRepository;
    private final BookCouponApiClient bookCouponApiClient;
    private final PayService payService;

    @Override
    @Transactional(readOnly = true)
    public List<OrderInfoDTO> getOrderInfos(Long orderGroupId) {
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        List<OrderInfoDTO> orderInfoList = new ArrayList<>();
        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {

            String bookName = bookCouponApiClient.getBookName(orderDetailResponseDTO.getBookId());

            OrderInfoDTO orderInfoDTO = new OrderInfoDTO(
                    orderDetailResponseDTO.getId(),
                    orderDetailResponseDTO.getOrderStatus(),
                    bookName,
                    orderDetailResponseDTO.getAmount(),
                    orderDetailResponseDTO.getDiscountPrice(),
                    orderDetailResponseDTO.getPrimePrice());

            orderInfoList.add(orderInfoDTO);
        }
        return orderInfoList;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderGroupInfoDTO getOrderGroupInfo(Long orderGroupId) {
        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.getOrderGroupById(orderGroupId);
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        WrappingResponseDTO wrappingResponseDTO = wrappingService.getWrappingById(orderGroupResponseDTO.getWrappingId());

        int usedPoint = pointHistoryService.getUsedPoint(orderGroupId);
        int earnedPoint = pointHistoryService.getEarnedPoint(orderGroupId);
        // 판매가 총합
        int primeTotalPrice = 0;
        // 할인 금액
        int discountPrice = 0;

        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {
            primeTotalPrice += orderDetailResponseDTO.getPrimePrice();
            discountPrice += orderDetailResponseDTO.getDiscountPrice();
        }

        // 총 계산된 금액
        int totalPrice = primeTotalPrice - discountPrice + wrappingResponseDTO.getPrice() + orderGroupResponseDTO.getDeliveryPrice();

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

    @Override
    @Transactional(readOnly = true)
    public DeliveryInfoDTO getDeliveryInfo(Long orderGroupId) {
        return queryDslDeliveryInfoRepository.getDeliveryInfo(orderGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPayInfoDTO getOrderPayInfo(Long orderGroupId) {
        return payService.getOrderPayInfo(orderGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPayDetailDTO getOrderPayDetail(Long orderGroupId) {
        return new OrderPayDetailDTO(
                getOrderInfos(orderGroupId),
                getOrderGroupInfo(orderGroupId),
                getDeliveryInfo(orderGroupId),
                getOrderPayInfo(orderGroupId)
        );
    }


}
