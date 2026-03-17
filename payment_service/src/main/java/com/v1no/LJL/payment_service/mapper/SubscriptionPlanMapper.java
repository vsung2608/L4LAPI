package com.v1no.LJL.payment_service.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.v1no.LJL.payment_service.model.dto.request.CreateSubscriptionPlanRequest;
import com.v1no.LJL.payment_service.model.dto.response.SubscriptionPlanResponse;
import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;
import com.v1no.LJL.payment_service.model.enums.PlanType;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlan toEntity(CreateSubscriptionPlanRequest request) {
        return SubscriptionPlan.builder()
            .code(request.getCode())
            .name(request.getName())
            .price(request.getPrice())
            .durationDays(request.getDurationDays())
            .discount(request.getDiscount())
            .type(PlanType.valueOf(request.getType()))
            .isActive(true)
            .build();
    }

    public SubscriptionPlanResponse toResponse(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
            .id(plan.getId())
            .code(plan.getCode())
            .name(plan.getName())
            .price(plan.getPrice())
            .finalPrice(plan.getPrice().multiply(BigDecimal.valueOf(100 - plan.getDiscount()).divide(BigDecimal.valueOf(100))))
            .durationDays(plan.getDurationDays())
            .discount(plan.getDiscount())
            .type(plan.getType().name())
            .isActive(plan.getIsActive())
            .createdAt(plan.getCreatedAt())
            .build();
    }
}
