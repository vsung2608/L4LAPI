package com.v1no.LJL.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.error("Feign error: {} - Status: {}", methodKey, response.status());
            
            switch (response.status()) {
                case 400:
                    return new IllegalArgumentException("Bad request to Google API");
                case 401:
                    return new RuntimeException("Unauthorized - Invalid token");
                case 403:
                    return new RuntimeException("Forbidden - Access denied");
                case 404:
                    return new RuntimeException("Google API endpoint not found");
                default:
                    return new RuntimeException("Google API error: " + response.status());
            }
        };
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Accept", "application/json");
            requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
        };
    }
}
