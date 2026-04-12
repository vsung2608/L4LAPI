package com.v1no.LJL.payment_service.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "${services.auth-service.url}")
public interface AuthServiceClient {
    @PutMapping("/api/v1/users/internal/{userId}/upgrade-vip")
    void upgradeToVip(
        @PathVariable("userId") UUID userId,
        @RequestParam("planCode") String planCode,
        @RequestParam(value = "durationDays", required = false, defaultValue = "30") Integer durationDays
    );
}
