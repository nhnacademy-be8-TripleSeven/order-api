package com.tripleseven.orderapi.entity.pay;

public enum PaymentStatus {
    READY("READY"), // 초기 상태
    IN_PROGRESS("IN_PROGRESS"), // 인증 완료 상태
    WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT"), // 가상계좌 입금 대기
    DONE("DONE"), // 결제 완료
    CANCELED("CANCELED"), // 결제 취소
    PARTIAL_CANCELED("PARTIAL_CANCELED"), // 부분 취소
    ABORTED("ABORTED"), // 결제 실패
    EXPIRED("EXPIRED"); // 결제 유효 시간 만료

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PaymentStatus fromString(String status) {
        for (PaymentStatus s : PaymentStatus.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + status);
    }
}