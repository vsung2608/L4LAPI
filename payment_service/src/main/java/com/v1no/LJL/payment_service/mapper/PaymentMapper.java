package com.v1no.LJL.payment_service.mapper;

import org.springframework.stereotype.Component;

import com.v1no.LJL.payment_service.model.dto.response.PaymentDetailResponse;
import com.v1no.LJL.payment_service.model.dto.response.PaymentResponse;
import com.v1no.LJL.payment_service.model.entity.Payment;
import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .userId(payment.getUserId())
            .orderId(payment.getOrderId())
            .amount(payment.getAmount())
            .status(payment.getStatus().name())
            .planCode(payment.getPlan().getCode())
            .planName(payment.getPlan().getName())
            .paymentUrl(payment.getPaymentUrl())
            .createdAt(payment.getCreatedAt())
            .build();
    }

    public PaymentDetailResponse toDetailResponse(Payment payment) {
        SubscriptionPlan plan = payment.getPlan();
        
        return PaymentDetailResponse.builder()
            .id(payment.getId())
            .userId(payment.getUserId())
            .orderId(payment.getOrderId())
            .amount(payment.getAmount())
            .status(payment.getStatus().name())
            .planId(plan.getId())
            .planCode(plan.getCode())
            .planName(plan.getName())
            .durationDays(plan.getDurationDays())
            .vnpTransactionNo(payment.getVnpTransactionNo())
            .vnpTxnRef(payment.getVnpTxnRef())
            .vnpResponse(payment.getVnpResponse())
            .paidAt(payment.getPaidAt())
            .createdAt(payment.getCreatedAt())
            .build();
    }
}
