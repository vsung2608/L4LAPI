package com.v1no.LJL.auth_service.config;

import java.util.HashMap;

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
            log.error("Feign error: {} - Status: {} = Message: {}", methodKey, response.status(), response.reason());
            
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

            if (requestTemplate.method().equals("POST")) {
                // DEBUG - log what's actually being sent
                log.debug("Query params before encoding: {}", requestTemplate.queries());
                log.debug("Existing body: {}", 
                    requestTemplate.body() != null ? new String(requestTemplate.body()) : "null");

                String queryLine = requestTemplate.queryLine();
                log.debug("Query line: {}", queryLine);

                if (queryLine != null && queryLine.length() > 1) {
                    String body = queryLine.substring(1);
                    requestTemplate.body(body);
                    requestTemplate.queries(new HashMap<>());
                    requestTemplate.header("Content-Length", String.valueOf(body.length()));
                    log.debug("Final encoded body: {}", body);
                } else {
                    log.warn("Query line is empty — params may already be in body or missing entirely");
                }
            }
        };
    }
}
