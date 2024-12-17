//package com.example.orderapi.service.pointservice;
//
//import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
//import com.example.orderapi.entity.pointhistory.HistoryTypes;
//import com.example.orderapi.entity.pointhistory.PointHistory;
//import com.example.orderapi.repository.pointhistory.PointHistoryRepository;
//import com.example.orderapi.repository.pointpolicy.PointPolicyRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class PointServiceImpl implements PointService{
//
//    private final PointHistoryRepository pointHistoryRepository;
//    private final PointPolicyRepository pointPolicyRepository;
//    @Override
//    public PointHistoryResponse createPointHistoryForPaymentSpend(Long memberId, int usePoint) {
//        PointHistory pointHistory = new PointHistory(
//                null,
//                HistoryTypes.SPEND,
//                usePoint * -1,
//                LocalDateTime.now(),
//
//
//        );
//        return null;
//    }
//
//    @Override
//    public PointHistoryResponse createPointHistoryForPaymentEarn(Long memberId, int payAmount, Long pointPolicyId) {
//        return null;
//    }
//}
