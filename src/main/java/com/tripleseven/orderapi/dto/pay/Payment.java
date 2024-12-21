package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Payment {
    private String paymentKey;    // 결제 고유 키
    private Long orderId;         // 주문 아이디
    private Long totalAmount;     // 총 결제 금액
    private String type;          // 결제 타입 (일반결제, 자동결제, 등)
    private String orderName;     // 주문 이름
    private String currency;      // 결제 통화
    private String method;        // 결제 수단 (카드, 가상계좌 등)
    private String status;        // 결제 상태 (READY, IN_PROGRESS, DONE 등)
    private String requestedAt;   // 결제 요청 시각
    private String approvedAt;    // 결제 승인 시각 (nullable)
    private Long balanceAmount;   // 취소할 수 있는 금액(결제 취소나 부분취소가 되고 나서 남은 값)
    private String receipt;       // 결제 영수증 정보
}
