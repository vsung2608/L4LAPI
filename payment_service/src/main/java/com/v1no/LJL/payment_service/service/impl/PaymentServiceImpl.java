package com.v1no.LJL.payment_service.service.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.payment_service.client.AuthServiceClient;
import com.v1no.LJL.payment_service.mapper.PaymentMapper;
import com.v1no.LJL.payment_service.model.dto.request.CreatePaymentRequest;
import com.v1no.LJL.payment_service.model.dto.response.PaymentDetailResponse;
import com.v1no.LJL.payment_service.model.dto.response.PaymentResponse;
import com.v1no.LJL.payment_service.model.dto.response.VNPayCallbackResponse;
import com.v1no.LJL.payment_service.model.entity.Payment;
import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;
import com.v1no.LJL.payment_service.repository.PaymentRepository;
import com.v1no.LJL.payment_service.repository.SubscriptionPlanRepository;
import com.v1no.LJL.payment_service.service.VNPayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {
    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanRepository planRepository;
    private final VNPayService vnPayService;
    private final AuthServiceClient authServiceClient;
    private final PaymentMapper paymentMapper;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public PaymentResponse createPayment(UUID userId, CreatePaymentRequest request, String ipAddress) {
        log.info("Creating payment for user: {}, plan: {}", userId, request.getPlanCode());

        SubscriptionPlan plan = planRepository.findByCode(request.getPlanCode())
            .orElseThrow(() -> new RuntimeException("Plan not found: " + request.getPlanCode()));

        if (!plan.getIsActive()) {
            throw new RuntimeException("Plan is not active: " + request.getPlanCode());
        }

        String orderId = Payment.generateOrderId();

        BigDecimal amount = plan.getPrice()
                                .multiply(BigDecimal.valueOf((100 - plan.getDiscount()) / 100));

        Payment payment = Payment.builder()
            .userId(userId)
            .plan(plan)
            .orderId(orderId)
            .amount(amount)
            .status(Payment.Status.PENDING)
            .build();

        String orderInfo = "Thanh toan " + plan.getName();
        String paymentUrl = vnPayService.createPaymentUrl(
            orderId,
            amount.longValue(),
            orderInfo,
            ipAddress
        );

        payment.setPaymentUrl(paymentUrl);
        payment.setVnpTxnRef(orderId);

        payment = paymentRepository.save(payment);

        log.info("Payment created: orderId={}, amount={}", orderId, amount);

        return paymentMapper.toResponse(payment);
    }

    public VNPayCallbackResponse handleVNPayCallback(Map<String, String> vnpParams) {
        log.info("Handling VNPay callback");

        String orderId = vnpParams.get("vnp_TxnRef");
        String responseCode = vnpParams.get("vnp_ResponseCode");
        String transactionNo = vnpParams.get("vnp_TransactionNo");

        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + orderId));

        // Verify signature
        boolean isValid = vnPayService.verifyCallback(vnpParams);

        if (!isValid) {
            log.error("Invalid VNPay signature for order: {}", orderId);
            return VNPayCallbackResponse.builder()
                .success(false)
                .message("Invalid signature")
                .orderId(orderId)
                .redirectUrl(frontendUrl + "/payment/failed?orderId=" + orderId)
                .build();
        }

        if ("00".equals(responseCode)) {
            payment.markAsSuccess(transactionNo, vnpParams);
            paymentRepository.save(payment);

            log.info("Payment successful: orderId={}, transactionNo={}", orderId, transactionNo);

            try {
                upgradeUserToVip(payment);
            } catch (Exception e) {
                log.error("Failed to upgrade user to VIP", e);
            }

            return VNPayCallbackResponse.builder()
                .success(true)
                .message("Payment successful")
                .orderId(orderId)
                .redirectUrl(frontendUrl + "/payment/success?orderId=" + orderId)
                .build();

        } else {
            // Payment failed
            payment.markAsFailed();
            paymentRepository.save(payment);

            log.warn("Payment failed: orderId={}, responseCode={}", orderId, responseCode);

            return VNPayCallbackResponse.builder()
                .success(false)
                .message("Payment failed: " + responseCode)
                .orderId(orderId)
                .redirectUrl(frontendUrl + "/payment/failed?orderId=" + orderId)
                .build();
        }
    }

    private void upgradeUserToVip(Payment payment) {
        log.info("Upgrading user to VIP: userId={}", payment.getUserId());

        SubscriptionPlan plan = payment.getPlan();

        authServiceClient.upgradeToVip(
            payment.getUserId(),
            plan.getCode(),
            plan.getDurationDays()
        );

        log.info("User upgraded to VIP successfully");
    }


    @Transactional(readOnly = true)
    public Page<PaymentDetailResponse> getUserPayments(UUID userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
            .map(paymentMapper::toDetailResponse);
    }

    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetail(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        return paymentMapper.toDetailResponse(payment);
    }

    /**
     * Get payment by order ID
     */
    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + orderId));

        return paymentMapper.toDetailResponse(payment);
    }
}
