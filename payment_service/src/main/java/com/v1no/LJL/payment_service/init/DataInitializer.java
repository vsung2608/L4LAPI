package com.v1no.LJL.payment_service.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;
import com.v1no.LJL.payment_service.model.enums.PlanType;
import com.v1no.LJL.payment_service.repository.SubscriptionPlanRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(String... args) {
        if (planRepository.count() == 0) {
            log.info("Initializing subscription plans...");
            initSubscriptionPlans();
            log.info("Subscription plans initialized successfully!");
        } else {
            log.info("Subscription plans already exist. Skipping initialization.");
        }
    }

    private void initSubscriptionPlans() {
        List<SubscriptionPlan> plans = Arrays.asList(
            // ========== BASIC PLANS ==========
            SubscriptionPlan.builder()
                .code("BASIC_MONTHLY")
                .name("Gói Basic - Tháng")
                .price(new BigDecimal("49000"))
                .durationDays(30)
                .discount(0)
                .type(PlanType.BASIC)
                .isActive(true)
                .build(),

            SubscriptionPlan.builder()
                .code("BASIC_YEARLY")
                .name("Gói Basic - Năm")
                .price(new BigDecimal("490000"))
                .durationDays(365)
                .discount(15)
                .type(PlanType.BASIC)
                .isActive(true)
                .build(),

            // ========== PLUS PLANS ==========
            SubscriptionPlan.builder()
                .code("PLUS_MONTHLY")
                .name("Gói Plus - Tháng")
                .price(new BigDecimal("99000"))
                .durationDays(30)
                .discount(0)
                .type(PlanType.PLUS)
                .isActive(true)
                .build(),

            SubscriptionPlan.builder()
                .code("PLUS_YEARLY")
                .name("Gói Plus - Năm")
                .price(new BigDecimal("990000"))
                .durationDays(365)
                .discount(15)
                .type(PlanType.PLUS)
                .isActive(true)
                .build(),

            // ========== PREMIUM PLANS ==========
            SubscriptionPlan.builder()
                .code("PREMIUM_MONTHLY")
                .name("Gói Premium - Tháng")
                .price(new BigDecimal("149000"))
                .durationDays(30)
                .discount(0)
                .type(PlanType.PREMIUM)
                .isActive(true)
                .build(),

            SubscriptionPlan.builder()
                .code("PREMIUM_YEARLY")
                .name("Gói Premium - Năm")
                .price(new BigDecimal("1490000"))
                .durationDays(365)
                .discount(15)
                .type(PlanType.PREMIUM)
                .isActive(true)
                .build(),

            // ========== FULLLIFE PLAN ==========
            SubscriptionPlan.builder()
                .code("FULLLIFE")
                .name("Gói Trọn Đời")
                .price(new BigDecimal("2990000"))
                .durationDays(null)
                .discount(0)
                .type(PlanType.PREMIUM)
                .isActive(true)
                .build()
        );

        planRepository.saveAll(plans);
        log.info("Saved {} subscription plans", plans.size());
    }
}
