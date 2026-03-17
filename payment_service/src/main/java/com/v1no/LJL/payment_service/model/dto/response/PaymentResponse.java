package com.v1no.LJL.payment_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private UUID userId;
    private String orderId;
    private BigDecimal amount;
    private String status;
    private String planCode;
    private String planName;
    private String paymentUrl;
    private LocalDateTime createdAt;
}
