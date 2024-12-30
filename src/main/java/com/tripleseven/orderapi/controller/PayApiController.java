package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.pay.OrderInfoDTO;
import com.tripleseven.orderapi.dto.pay.OrderInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.OrderInfoResponseDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.service.pay.PayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(name = "Payment API", description = "결제 관련 API를 제공합니다.")
public class PayApiController {

    @Value("${payment.toss.test_widget_api_key}")
    private String WIDGET_SECRET_KEY;
    @Value("${payment.toss.test_secret_api_key}")
    private String API_SECRET_KEY;
    private final Map<String, String> billingKeyMap = new HashMap<>();
    private final PayService payService;
    private final PointHistoryService pointHistoryService;

    @Operation(summary = "결제 승인 요청", description = "Widget 또는 Payment 방식을 이용하여 결제를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "결제 승인 실패")
    })
    @PostMapping(value = {"/confirm/widget", "/confirm/payment"})
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestHeader("X-USER")Long userId,@RequestBody String jsonBody) throws Exception {
        String secretKey = request.getRequestURI().contains("/confirm/payment") ? API_SECRET_KEY : WIDGET_SECRET_KEY;
        JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");

        int statusCode = response.containsKey("error") ? 400 : 200;

        if(statusCode == 200){
            payService.save(userId,response);   //redis에 저장한것 mysql에 저장
        }
        return ResponseEntity.status(statusCode).body(response);
    }

    //결제 취소 기능, 배송 이전엔 사용자가 주문 취소 가능, 배송 이후엔 반품 신청 등을 해야 주문 취소
    @Operation(summary = "결제 취소", description = "결제 키를 사용하여 결제를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "400", description = "결제 취소 실패")
    })
    @PostMapping("/payments/{paymentKey}/cancel")
    public ResponseEntity<JSONObject> cancelPayment(@PathVariable("paymentKey") String paymentKey, @RequestBody PayCancelRequestDTO request) throws Exception {
        JSONObject requestData = convertToJSONObject(request);
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);
        payService.payCancel(response);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @Operation(summary = "결제 조회 (PaymentKey)", description = "PaymentKey를 사용하여 결제 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @ApiResponse(responseCode = "400", description = "결제 조회 실패")
    })
    @GetMapping("/payments/{paymentKey}")
    public ResponseEntity<JSONObject> getPayment(@PathVariable("paymentKey") String paymentKey) throws Exception {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;
        JSONObject response = sendRequest(new JSONObject(), API_SECRET_KEY, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    //구현 못함
    @Operation(summary = "결제 조회 (OrderId)", description = "OrderId를 사용하여 결제 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @ApiResponse(responseCode = "400", description = "결제 조회 실패")
    })
    @GetMapping("/payments/orders/{orderId}")
    public ResponseEntity<JSONObject> getOrder(@PathVariable("orderId") String orderId) throws Exception {
        String url = "https://api.tosspayments.com/v1/orders/" + orderId;
        JSONObject response = sendRequest(new JSONObject(), API_SECRET_KEY, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @PostMapping("/payments/order")
    public ResponseEntity<OrderInfoResponseDTO> responseOrderInfo(
            @RequestHeader("X-USER")Long userId,
            @RequestBody OrderInfoRequestDTO request) throws Exception {
        OrderInfoResponseDTO response = payService.getOrderInfo(userId,request);
        return ResponseEntity.ok(response);
    }
//
//    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
//        HttpURLConnection connection = createConnection(secretKey, urlString);
//        try (OutputStream os = connection.getOutputStream()) {
//            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
//        }
//
//        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
//             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
//            return (JSONObject) new JSONParser().parse(reader);
//        } catch (Exception e) {
//            JSONObject errorResponse = new JSONObject();
//            errorResponse.put("error", "Error reading response");
//            return errorResponse;
//        }
//    }

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