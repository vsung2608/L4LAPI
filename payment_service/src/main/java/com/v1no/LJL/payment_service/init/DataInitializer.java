package com.v1no.LJL.payment_service.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.v1no.LJL.payment_service.model.entity.SubscriptionPlan;
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
            SubscriptionPlan.builder()
                .code("MONTHLY")
                .name("Gói tháng")
                .description("Truy cập đầy đủ tính năng trong 30 ngày, linh hoạt, dễ bắt đầu.")
                .price(new BigDecimal("49000"))
                .durationDays(30)
                .discount(0)
                .isActive(true)
                .build(),

            SubscriptionPlan.builder()
                .code("QUARTER")
                .name("Gói Quý")
                .description("Tiết kiệm hơn khi đăng ký 3 tháng, phù hợp học liên tục.")
                .price(new BigDecimal("147000"))
                .durationDays(90)
                .discount(5)
                .isActive(true)
                .build(),

            SubscriptionPlan.builder()
                .code("YEARLY")
                .name("Gói năm")
                .description("Truy cập dài hạn 12 tháng với chi phí tối ưu nhất.")
                .price(new BigDecimal("1288000"))
                .durationDays(365)
                .discount(15)
                .isActive(true)
                .build(),

            SubscriptionPlan.builder()
                .code("FULLLIFE")
                .name("Gói vĩnh viễn")
                .description("CThanh toán một lần, học trọn đời, không giới hạn thời gian.")
                .price(new BigDecimal("2990000"))
                .durationDays(null)
                .discount(0)
                .isActive(true)
                .build()
        );

        planRepository.saveAll(plans);
        log.info("Saved {} subscription plans", plans.size());
    }
}
