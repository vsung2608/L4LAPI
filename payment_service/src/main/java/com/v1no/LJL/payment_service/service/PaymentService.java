package com.v1no.LJL.payment_service.service;

import java.util.Map;
import java.util.UUID;

import com.v1no.LJL.common.dto.PageResponse;
import com.v1no.LJL.payment_service.model.dto.request.CreatePaymentRequest;
import com.v1no.LJL.payment_service.model.dto.response.PaymentDetailResponse;
import com.v1no.LJL.payment_service.model.dto.response.PaymentResponse;
import com.v1no.LJL.payment_service.model.dto.response.VNPayCallbackResponse;

public interface PaymentService {
    PaymentResponse createPayment(UUID userId, CreatePaymentRequest request, String ipAddress);
    VNPayCallbackResponse handleVNPayCallback(Map<String, String> vnpParams);
    PageResponse<PaymentDetailResponse> getUserPayments(UUID userId, int page, int size);
    PageResponse<PaymentDetailResponse> getPayments(int page, int size);
    PaymentDetailResponse getPaymentDetail(UUID paymentId);
    PaymentDetailResponse getPaymentByOrderId(String orderId);
}
