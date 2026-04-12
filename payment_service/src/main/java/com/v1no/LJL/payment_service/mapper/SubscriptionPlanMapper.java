package com.v1no.LJL.payment_service.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.v1no.LJL.payment_service.model.dto.request.CreateSubscriptionPlanRequest;
import com.v1no.LJL.payment_service.model.dto.response.SubscriptionPlanResponse;
import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlan toEntity(CreateSubscriptionPlanRequest request) {
        return SubscriptionPlan.builder()
            .code(request.getCode())
            .name(request.getName())
            .price(request.getPrice())
            .durationDays(request.getDurationDays())
            .discount(request.getDiscount())
            .isActive(true)
            .build();
    }

    public SubscriptionPlanResponse toResponse(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
            .id(plan.getId())
            .code(plan.getCode())
            .name(plan.getName())
            .description(plan.getDescription())
            .price(plan.getPrice())
            .finalPrice(plan.getPrice().multiply(BigDecimal.valueOf(100 - plan.getDiscount()).divide(BigDecimal.valueOf(100))))
            .durationDays(plan.getDurationDays())
            .discount(plan.getDiscount())
            .isActive(plan.getIsActive())
            .createdAt(plan.getCreatedAt())
            .build();
    }
}
