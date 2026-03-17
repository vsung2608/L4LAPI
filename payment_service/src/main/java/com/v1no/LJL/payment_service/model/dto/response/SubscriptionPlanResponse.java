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
public class SubscriptionPlanResponse {

    private UUID id;
    private String code;
    private String name;
    private BigDecimal price;
    private BigDecimal finalPrice;
    private Integer durationDays;
    private Integer discount;
    private String type;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
