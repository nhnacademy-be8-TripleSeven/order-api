package com.tripleseven.orderapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    API_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 API 요청입니다."),
    CANCEL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "취소 요청을 실패하였습니다."),
    REFUND_BAD_REQUEST(HttpStatus.BAD_REQUEST, "반품 요청을 실패하였습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 사용자 입니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 Resource 가 없습니다."),
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 ID에 맞는 데이터를 찾을 수 없습니다."),
    REDIS_NOT_FOUND(HttpStatus.NOT_FOUND, "Redis 값을 찾을 수 없습니다."),
    PAY_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰 정보를 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ALREADY_EXIST_CONFLICT(HttpStatus.CONFLICT, "이미 존재하고 있습니다."),
    POINT_FAILED_CONFLICT(HttpStatus.CONFLICT, "포인트가 충분하지 않습니다."),
    AMOUNT_FAILED_CONFLICT(HttpStatus.CONFLICT, "수량이 충분하지 않습니다."),

    /* 422 Unprocessable Entity: 요청 데이터는 올바르지만, 처리할 수 없는 비즈니스 조건이 존재. */
    RETURN_EXPIRED_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "30일 환불 기간 만료되었습니다."),
    COUPON_USED_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "잘못된 쿠폰 사용 방법입니다."),
    POINT_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "포인트를 사용할 수 없습니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러");

    private final HttpStatus httpStatus;
    private final String message;
}
