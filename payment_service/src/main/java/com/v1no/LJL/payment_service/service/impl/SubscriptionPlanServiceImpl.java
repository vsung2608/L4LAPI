package com.v1no.LJL.payment_service.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.v1no.LJL.payment_service.mapper.SubscriptionPlanMapper;
import com.v1no.LJL.payment_service.model.dto.request.CreateSubscriptionPlanRequest;
import com.v1no.LJL.payment_service.model.dto.response.SubscriptionPlanResponse;
import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;
import com.v1no.LJL.payment_service.model.enums.PlanType;
import com.v1no.LJL.payment_service.repository.SubscriptionPlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl {
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getAllActivePlans() {
        return planRepository.findByIsActiveTrue().stream()
            .map(planMapper::toResponse)
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getVipPlans() {
        return planRepository.findByTypeAndIsActiveTrue(PlanType.PREMIUM).stream()
            .map(planMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getPlanByCode(String code) {
        SubscriptionPlan plan = planRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Plan not found: " + code));
        
        return planMapper.toResponse(plan);
    }

    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getPlanById(UUID id) {
        SubscriptionPlan plan = planRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found: " + id));
        
        return planMapper.toResponse(plan);
    }

    public SubscriptionPlanResponse createPlan(CreateSubscriptionPlanRequest request) {
        log.info("Creating new subscription plan: {}", request.getCode());

        if (planRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Plan code already exists: " + request.getCode());
        }

        SubscriptionPlan plan = planMapper.toEntity(request);
        plan = planRepository.save(plan);

        log.info("Subscription plan created: {}", plan.getCode());
        return planMapper.toResponse(plan);
    }

    public SubscriptionPlanResponse updatePlan(UUID id, CreateSubscriptionPlanRequest request) {
        log.info("Updating subscription plan: {}", id);

        SubscriptionPlan plan = planRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found: " + id));

        if (!plan.getCode().equals(request.getCode()) && 
            planRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Plan code already exists: " + request.getCode());
        }

        plan.setCode(request.getCode());
        plan.setName(request.getName());
        plan.setPrice(request.getPrice());
        plan.setDurationDays(request.getDurationDays());
        plan.setDiscount(request.getDiscount());
        plan.setType(PlanType.valueOf(request.getType()));

        plan = planRepository.save(plan);

        log.info("Subscription plan updated: {}", plan.getCode());
        return planMapper.toResponse(plan);
    }

    public void deactivatePlan(UUID id) {
        log.info("Deactivating subscription plan: {}", id);

        SubscriptionPlan plan = planRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Plan not found: " + id));

        plan.setIsActive(false);
        planRepository.save(plan);

        log.info("Subscription plan deactivated: {}", plan.getCode());
    }
}
