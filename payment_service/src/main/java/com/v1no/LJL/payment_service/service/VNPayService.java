package com.v1no.LJL.payment_service.service;

import java.util.Map;

public interface VNPayService {
    String createPaymentUrl(String orderId, long amount, String orderInfo, String ipAddress);
    boolean verifyCallback(Map<String, String> vnpParams);
}
