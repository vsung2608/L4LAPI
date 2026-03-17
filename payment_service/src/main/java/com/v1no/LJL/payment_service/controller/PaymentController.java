package com.v1no.LJL.payment_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.payment_service.model.dto.request.CreatePaymentRequest;
import com.v1no.LJL.payment_service.model.dto.response.PaymentDetailResponse;
import com.v1no.LJL.payment_service.model.dto.response.PaymentResponse;
import com.v1no.LJL.payment_service.model.dto.response.VNPayCallbackResponse;
import com.v1no.LJL.payment_service.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

   
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
        @RequestHeader("X-User_id") UUID userId,
        @Valid @RequestBody CreatePaymentRequest request,
        HttpServletRequest httpRequest
    ) {
        String ipAddress = getClientIp(httpRequest);
        
        PaymentResponse response = paymentService.createPayment(userId, request, ipAddress);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<VNPayCallbackResponse> vnpayReturn(@RequestParam Map<String, String> allParams) {
        log.info("VNPay return callback received");
        
        VNPayCallbackResponse response = paymentService.handleVNPayCallback(allParams);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<PaymentDetailResponse>> getPaymentHistory(
        @RequestHeader("X-User_id") UUID userId,
        Pageable pageable
    ) {
        Page<PaymentDetailResponse> payments = paymentService.getUserPayments(userId, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDetailResponse> getPaymentDetail(
        @PathVariable UUID paymentId
    ) {
        PaymentDetailResponse payment = paymentService.getPaymentDetail(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDetailResponse> getPaymentByOrderId(
        @PathVariable String orderId
    ) {
        PaymentDetailResponse payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
