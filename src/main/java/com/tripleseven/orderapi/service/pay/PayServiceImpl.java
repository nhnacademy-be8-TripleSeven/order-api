package com.tripleseven.orderapi.service.pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.cartitem.OrderItemDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import com.tripleseven.orderapi.dto.coupon.CouponStatus;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.*;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyService;
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
    private final OrderGroupService orderGroupService;
    private final PointHistoryService pointHistoryService;
    private final PointPolicyService pointPolicyService;

    private final BookCouponApiClient bookCouponApiClient;

    private final RedisTemplate<String, Object> redisTemplate;

    // TODO save 해서 반환해야될 경우가 있는지 확인(void 타입)
    @Override
    public void createPay(Long userId, JSONObject jsonObject) {
        Pay pay = new Pay();
        PayInfoDTO infoDto = (PayInfoDTO) redisTemplate.opsForHash().get(userId.toString(), "PayInfo");
        //infoDTO를 각 db에 저장해야함

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

        // 정상 응답 DTO 반환
        return PaymentDTO.fromJson(response);
    }

    @Override
    public PayInfoResponseDTO createPayInfo(Long userId, String guestId, PayInfoRequestDTO request) {
        long orderId = UUID.randomUUID().getMostSignificantBits();
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.ofCreate(orderId, request);

//        checkValid(userId, payInfoDTO);

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
    public PaymentDTO getPaymentInfo(String paymentKey) {
        String secretKey = apiProperties.getSecretApiKey();
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;

        return null;
    }


    private void checkValid(Long userId, PayInfoDTO payInfo) {
        List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();
        Long couponId = payInfo.getCouponId();
        Long usePoint = payInfo.getPoint();
        Long totalAmount = payInfo.getTotalAmount();


        Map<Long, Integer> bookAmounts = new HashMap<>();
        List<Long> bookIds = new ArrayList<>();

        for (OrderBookInfoDTO bookInfo : bookInfos) {
            bookIds.add(bookInfo.getBookId());
            bookAmounts.put(bookInfo.getBookId(), bookInfo.getQuantity());
        }

        List<OrderItemDTO> realItems = bookCouponApiClient.getOrderItems(bookIds);

        // 재고 검증
        checkAmount(bookAmounts, realItems);

        // 쿠폰 검증
        checkCoupon(couponId, bookInfos);

        // 포인트 검증
        checkPoint(userId, totalAmount, usePoint);
    }

    private void checkAmount(Map<Long, Integer> bookAmounts, List<OrderItemDTO> realItems) {
        for (OrderItemDTO realItem : realItems) {
            int amount = bookAmounts.get(realItem.getBookId());

            if (realItem.getAmount() > amount) {
                throw new CustomException(ErrorCode.AMOUNT_FAILED_CONFLICT);
            }
        }
    }

    private void checkCoupon(Long totalAmount, List<OrderBookInfoDTO> bookInfos) {
        CouponDTO coupon = bookCouponApiClient.getCoupon(1L);

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

        for (OrderBookInfoDTO bookInfo : bookInfos) {
            if (bookInfo.getCouponId() != null) {
                // 계산 재확인
                Long realDiscount = bookCouponApiClient.applyCoupon(bookInfo.getCouponId(), bookInfo.getPrice());

                // 계산된 할인 금액과 맞지 않음
                if (bookInfo.getCouponSalePrice() != realDiscount) {
                    throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);

                }
            }
        }

    }

    private Long checkPoint(Long userId, Long totalPrice, Long point) {
        int userPoint = pointHistoryService.getTotalPointByMemberId(userId);

        // 보유 포인트보다 포인트 사용량이 더 큰 경우
        if (userPoint < point) {
            throw new CustomException(ErrorCode.POINT_UNPROCESSABLE_ENTITY);
        }

        // 최종 가격보다 사용량이 더 큰 경우
        if (point > totalPrice) {
            point = point - totalPrice;
        }

        return point;
    }

    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        // GET 요청의 경우 requestData를 보내지 않음
        HttpURLConnection connection;
        if (requestData.isEmpty()) {
            connection = createConnection(secretKey, urlString, "GET");
        } else {
            connection = createConnection(secretKey, urlString, "POST");
            // POST 요청 시에는 requestData를 본문에 포함
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
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
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(request);
            return parseRequestData(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}




