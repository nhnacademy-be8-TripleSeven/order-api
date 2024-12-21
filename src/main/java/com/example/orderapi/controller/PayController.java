package com.example.orderapi.controller;

import com.example.orderapi.dto.pay.PayCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class PayController {

    @GetMapping("/payment")
    public String paymentPage(Model model) {
        // 주문 정보
        Long orderId = UUID.randomUUID().getMostSignificantBits();
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", 45000);
        model.addAttribute("orderName", "토스 결제 테스트");
        model.addAttribute("customerName", "조문기");
        model.addAttribute("customerEmail", "1234@gmail.com");

        return "payment/checkout";
    }

//        @GetMapping("/payment")
//    public String paymentPage(@RequestBody PayCreateRequest request, Model model) {
//        // 주문 정보
//        model.addAttribute("orderId", request.getOrderId());
//        model.addAttribute("amount", request.getAmount());
//        model.addAttribute("orderName", request.getOrderName());
//        model.addAttribute("customerName", request.getCustomerName());
//        model.addAttribute("customerEmail", request.getCustomerEmail());
//
//        return "payment/checkout";
//    }

    @GetMapping("/payment/success")
    public String paymentSuccessPage() {
        return "widget/success";
    }

    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "/fail";
    }
}