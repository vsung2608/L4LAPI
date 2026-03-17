package com.v1no.LJL.payment_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailResponse {

    private UUID id;
    private UUID userId;
    private String orderId;
    private BigDecimal amount;
    private String status;
    
    private UUID planId;
    private String planCode;
    private String planName;
    private Integer durationDays;
    
    private String vnpTransactionNo;
    private String vnpTxnRef;
    private Map<String, String> vnpResponse;
    
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
