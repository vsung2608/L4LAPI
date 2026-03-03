package com.v1no.LJL.api_gateway.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v1no.LJL.common.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @RequestMapping("/user-service")
    public ResponseEntity<ApiResponse<Void>> userServiceFallback() {
        return buildFallback("user-service");
    }

    @RequestMapping("/learning-service")
    public ResponseEntity<ApiResponse<Void>> learningServiceFallback() {
        return buildFallback("learning-service");
    }

    @RequestMapping("/progress-service")
    public ResponseEntity<ApiResponse<Void>> progressServiceFallback() {
        return buildFallback("progress-service");
    }

    @RequestMapping("/article-service")
    public ResponseEntity<ApiResponse<Void>> articleServiceFallback() {
        return buildFallback("article-service");
    }

    private ResponseEntity<ApiResponse<Void>> buildFallback(String serviceName) {
        log.warn("Fallback triggered for: {}", serviceName);
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.<Void>builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message(serviceName + " is currently unavailable. Please try again later.")
                .timestamp(LocalDateTime.now())
                .build());
    }
}
