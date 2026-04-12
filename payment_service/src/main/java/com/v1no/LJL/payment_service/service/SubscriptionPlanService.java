package com.v1no.LJL.payment_service.service;

import java.util.List;
import java.util.UUID;

import com.v1no.LJL.payment_service.model.dto.request.CreateSubscriptionPlanRequest;
import com.v1no.LJL.payment_service.model.dto.response.SubscriptionPlanResponse;

public interface SubscriptionPlanService {
    List<SubscriptionPlanResponse> getAllActivePlans();
    SubscriptionPlanResponse getPlanByCode(String code);
    SubscriptionPlanResponse getPlanById(UUID id);
    SubscriptionPlanResponse createPlan(CreateSubscriptionPlanRequest request);
    SubscriptionPlanResponse updatePlan(UUID id, CreateSubscriptionPlanRequest request);
    void deactivatePlan(UUID id);
}
