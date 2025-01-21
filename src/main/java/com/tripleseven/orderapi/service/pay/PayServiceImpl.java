package com.tripleseven.orderapi.service.pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import com.tripleseven.orderapi.dto.coupon.CouponStatus;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.*;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final ApiProperties apiProperties;
    private final PayRepository payRepository;
    private final PointHistoryService pointHistoryService;
    private final BookCouponApiClient bookCouponApiClient;
    private final OrderGroupRepository orderGroupRepository;
    private final PayTypesRepository payTypesRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // ✅ static final로 선언


    @Override
    public void createPay(PaymentDTO paymentDTO, Long orderGroupId, String payType) {
        Pay pay = new Pay();
        Optional<OrderGroup> orderGroup = orderGroupRepository.findById(orderGroupId);
        PayType PayType = payTypesRepository.findByName(payType);
        if (orderGroup.isEmpty()) {
            throw new IllegalArgumentException();
        }
        pay.ofCreate(paymentDTO, orderGroup.get(), PayType);
        payRepository.save(pay);
    }

    @Override
    public Object cancelRequest(String paymentKey, PayCancelRequestDTO request) throws IOException {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
        JSONObject response = sendRequest(convertToJSONObject(request), apiProperties.getSecretApiKey(), url);

        if (response.containsKey("error")) {
            // Error 객체 반환
            return ErrorDTO.fromJson(response);
        }

        Pay pay = payRepository.findByPaymentKey(paymentKey);
        PaymentDTO paymentDTO = PaymentDTO.fromJson(response);
        pay.ofUpdate(paymentDTO);

        // 정상 응답 DTO 반환
        return paymentDTO;
    }

    @Override
    public PayInfoResponseDTO createPayInfo(Long userId, String guestId, PayInfoRequestDTO request) {
        long orderId = UUID.randomUUID().getMostSignificantBits();
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.ofCreate(orderId, request);

        checkValid(userId, payInfoDTO);

        if (Objects.nonNull(userId)) {
            redisTemplate.opsForHash().put(userId.toString(), "PayInfo", payInfoDTO);
        } else {
            redisTemplate.opsForHash().put(guestId, "PayInfo", payInfoDTO);
        }
        return new PayInfoResponseDTO(orderId, request.getTotalAmount());
    }

    @Override
    public OrderPayInfoDTO getOrderPayInfo(Long orderId) {

        return payRepository.getDTOByOrderGroupId(orderId);
    }

    @Override
    public Object confirmRequest(HttpServletRequest request, String jsonBody) throws IOException {
        String secretKey = request.getRequestURI().contains("/confirm/payment") ? apiProperties.getSecretApiKey() : apiProperties.getWidgetApiKey();

        JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");

        if (response.containsKey("error")) {
            // Error 객체 반환
            return ErrorDTO.fromJson(response);
        }

        // 정상 응답 DTO 반환
        return PaymentDTO.fromJson(response);
    }

    @Override
    public Object getPaymentInfo(String paymentKey) throws IOException {
        String secretKey = apiProperties.getSecretApiKey();
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;
        JSONObject response = sendRequest(new JSONObject(), secretKey, url);

        if (response.containsKey("error")) {
            return ErrorDTO.fromJson(response);
        }

        return PaymentDTO.fromJson(response);
    }

    @Override
    public Long getOrderId(Long orderId) {
        Pay pay = payRepository.findByOrderId(orderId);
        return pay.getOrderGroup().getId();
    }

    @Override
    public Long getPayPrice(Long orderId) {
        Pay pay = payRepository.findByOrderId(orderId);
        return pay.getPrice();
    }


    private void checkValid(Long userId, PayInfoDTO payInfo) {
        List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();

        Long usePoint = payInfo.getPoint();
        Long totalAmount = payInfo.getTotalAmount();


        // 쿠폰 검증
        checkCoupon(totalAmount, bookInfos);

        // 포인트 검증
        checkPoint(userId, totalAmount, usePoint);
    }

    private void checkCoupon(Long totalAmount, List<OrderBookInfoDTO> bookInfos) {
        for (OrderBookInfoDTO bookInfo : bookInfos) {
            Long couponId = bookInfo.getCouponId();
            if (couponId == null) {
                continue;
            }
            // 계산 재확인
            Long realDiscount = bookCouponApiClient.applyCoupon(bookInfo.getCouponId(), bookInfo.getPrice());

            // 계산된 할인 금액과 맞지 않음
            if (bookInfo.getCouponSalePrice() != realDiscount) {
                throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);

            }

            CouponDTO coupon = bookCouponApiClient.getCoupon(couponId);

            // 쿠폰 존재 검증
            if (Objects.isNull(coupon)) {
                throw new CustomException(ErrorCode.COUPON_NOT_FOUND);
            }

            // 사용 가능한 쿠폰 확인
            if (!coupon.getCouponStatus().equals(CouponStatus.NOTUSED)) {
                throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);
            }

            // 최소 사용 금액보다 적음
            if (totalAmount < coupon.getCouponMinAmount()) {
                throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);
            }

        }


    }

    private void checkPoint(Long userId, Long totalPrice, Long point) {
        int userPoint = pointHistoryService.getTotalPointByMemberId(userId);

        // 보유 포인트보다 포인트 사용량이 더 큰 경우
        if (userPoint < point) {
            throw new CustomException(ErrorCode.POINT_UNPROCESSABLE_ENTITY);
        }

        // 최종 가격보다 사용량이 더 큰 경우
        if (point > totalPrice) {
            throw new CustomException(ErrorCode.POINT_UNPROCESSABLE_ENTITY);
        }

    }

    public JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString, requestData.isEmpty() ? "GET" : "POST");

        if (!requestData.isEmpty()) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (ParseException e) {
            log.error("Response parsing error: {}", e.getMessage());
            throw new IOException("응답 데이터를 파싱하는 도중 오류 발생", e);
        } catch (IOException e) {
            log.error("HTTP 요청 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    private HttpURLConnection createConnection(String secretKey, String urlString, String requestMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod(requestMethod);
        connection.setDoOutput(true);
        return connection;
    }

    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            return new JSONObject();
        }
    }

    private JSONObject convertToJSONObject(PayCancelRequestDTO request) {
        try {
            String jsonString = OBJECT_MAPPER.writeValueAsString(request);
            return parseRequestData(jsonString);
        } catch (Exception e) {
            log.error("JSON 변환 오류: {}", e.getMessage());
            throw new CustomException(ErrorCode.JSON_PARSING_ERROR);
        }
    }
}




