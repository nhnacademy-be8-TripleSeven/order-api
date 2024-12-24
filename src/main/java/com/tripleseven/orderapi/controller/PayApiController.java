package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.service.pay.PayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class PayApiController {

    @Value("${payment.toss.test_widget_api_key}")
    private String WIDGET_SECRET_KEY;
    @Value("${payment.toss.test_secret_api_key}")
    private String API_SECRET_KEY;
    private final Map<String, String> billingKeyMap = new HashMap<>();
    private final PayService payService;

    @RequestMapping(value = {"/confirm/widget", "/confirm/payment"}, method = RequestMethod.POST)
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
        String secretKey = request.getRequestURI().contains("/confirm/payment") ? API_SECRET_KEY : WIDGET_SECRET_KEY;
        JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");

        payService.save(response);

        int statusCode = response.containsKey("error") ? 400 : 200;
        return ResponseEntity.status(statusCode).body(response);
    }

    @RequestMapping(value = "/confirm-billing", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> confirmBilling(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        String billingKey = billingKeyMap.get(requestData.get("customerKey"));
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, "https://api.tosspayments.com/v1/billing/" + billingKey);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/issue-billing-key", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> issueBillingKey(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, "https://api.tosspayments.com/v1/billing/authorizations/issue");
        if (!response.containsKey("error")) {
            billingKeyMap.put((String) requestData.get("customerKey"), (String) response.get("billingKey"));
        }

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/callback-auth", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> callbackAuth(@RequestParam String customerKey, @RequestParam String code) throws Exception {
        JSONObject requestData = new JSONObject();
        requestData.put("grantType", "AuthorizationCode");
        requestData.put("customerKey", customerKey);
        requestData.put("code", code);

        String url = "https://api.tosspayments.com/v1/brandpay/authorizations/access-token";
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    @RequestMapping(value = "/confirm/brandpay", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<JSONObject> confirmBrandpay(@RequestBody String jsonBody) throws Exception {
        JSONObject requestData = parseRequestData(jsonBody);
        String url = "https://api.tosspayments.com/v1/brandpay/payments/confirm";
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);

        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
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
            // Jackson ObjectMapper로 객체를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(request);

            // 기존 parseRequestData 메소드를 활용하여 JSON 문자열을 JSONObject로 변환
            return parseRequestData(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // 변환 실패 시 빈 JSONObject 반환
        }
    }

    //주문 취소
    @PostMapping("/payments/{paymentKey}/cancel")
    public ResponseEntity<JSONObject> cancelPayment(@PathVariable("paymentKey") String paymentKey, @RequestBody PayCancelRequestDTO request) throws Exception {
        JSONObject requestData = convertToJSONObject(request);
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey +"/cancel";
        JSONObject response = sendRequest(requestData, API_SECRET_KEY, url);
        payService.payCancel(response);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    //주문 조회 paymentKey
    @GetMapping("payments/{paymentKey}")
    public ResponseEntity<JSONObject> getPayment(@PathVariable("paymentKey") String paymentKey) throws Exception {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;
        JSONObject response = sendRequest(new JSONObject(), API_SECRET_KEY, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }

    //주문 조회 orderId
    @GetMapping("payments/orders/{orderId}")
    public ResponseEntity<JSONObject> getOrder(@PathVariable("orderId") String orderId) throws Exception {
        String url = "https://api.tosspayments.com/v1/orders/" + orderId;
        JSONObject response = sendRequest(new JSONObject(), API_SECRET_KEY, url);
        return ResponseEntity.status(response.containsKey("error") ? 400 : 200).body(response);
    }



    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
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

    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }
}