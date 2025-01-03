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
    public List<OrderInfoDTO> getOrderInfos(Long orderGroupId) {
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        List<OrderInfoDTO> orderInfoList = new ArrayList<>();
        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {

            String bookName = bookCouponApiClient.getBookName(orderDetailResponseDTO.getBookId());

            OrderInfoDTO orderInfoDTO = new OrderInfoDTO(
                    orderDetailResponseDTO.getStatus(),
                    bookName,
                    orderDetailResponseDTO.getAmount(),
                    orderDetailResponseDTO.getDiscountPrice(),
                    orderDetailResponseDTO.getPrimePrice());

            orderInfoList.add(orderInfoDTO);
        }
        return orderInfoList;
    }

    @Override
    public OrderGroupInfoDTO getOrderGroupInfo(Long orderGroupId) {
        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.getOrderGroupById(orderGroupId);
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        WrappingResponseDTO wrappingResponseDTO = wrappingService.getWrappingById(orderGroupResponseDTO.getWrappingId());

        int usedPoint = pointHistoryService.getUsedPoint(orderGroupId);
        int primeTotalPrice = 0;
        int discountPrice = 0;

        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {
            primeTotalPrice += orderDetailResponseDTO.getPrimePrice();
            discountPrice += orderDetailResponseDTO.getPrimePrice() - orderDetailResponseDTO.getDiscountPrice();
        }

        int totalPrice = primeTotalPrice - discountPrice;

        return new OrderGroupInfoDTO(
                primeTotalPrice,
                discountPrice,
                orderGroupResponseDTO.getDeliveryPrice(),
                wrappingResponseDTO.getName(),
                wrappingResponseDTO.getPrice(),
                totalPrice,
                usedPoint);
    }

    @Override
    public DeliveryInfoDTO getDeliveryInfo(Long orderGroupId) {
        return queryDslDeliveryInfoRepository.getDeliveryInfo(orderGroupId);
    }

    @Override
    public OrderPayInfoDTO getOrderPayInfo(Long orderGroupId) {
        return payService.getOrderPayInfo(orderGroupId);
    }

    @Override
    public OrderPayDetailDTO getOrderPayDetail(Long orderGroupId) {
        return new OrderPayDetailDTO(
                getOrderInfos(orderGroupId),
                getOrderGroupInfo(orderGroupId),
                getDeliveryInfo(orderGroupId),
                getOrderPayInfo(orderGroupId)
        );
    }


}
