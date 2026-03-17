package com.v1no.LJL.payment_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.payment_service.model.dto.request.CreateSubscriptionPlanRequest;
import com.v1no.LJL.payment_service.model.dto.response.SubscriptionPlanResponse;
import com.v1no.LJL.payment_service.service.SubscriptionPlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment/plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;


    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponse>> getAllActivePlans() {
        List<SubscriptionPlanResponse> plans = planService.getAllActivePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/vip")
    public ResponseEntity<List<SubscriptionPlanResponse>> getVipPlans() {
        List<SubscriptionPlanResponse> plans = planService.getVipPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{code}")
    public ResponseEntity<SubscriptionPlanResponse> getPlanByCode(@PathVariable String code) {
        SubscriptionPlanResponse plan = planService.getPlanByCode(code);
        return ResponseEntity.ok(plan);
    }


    @PostMapping
    public ResponseEntity<SubscriptionPlanResponse> createPlan(
        @Valid @RequestBody CreateSubscriptionPlanRequest request
    ) {
        SubscriptionPlanResponse plan = planService.createPlan(request);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> updatePlan(
        @PathVariable UUID id,
        @Valid @RequestBody CreateSubscriptionPlanRequest request
    ) {
        SubscriptionPlanResponse plan = planService.updatePlan(id, request);
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivatePlan(@PathVariable UUID id) {
        planService.deactivatePlan(id);
        return ResponseEntity.noContent().build();
    }
}
