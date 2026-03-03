package com.v1no.LJL.api_gateway.filter;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String userId = request.getHeaders().getFirst("X-User-Id");

        log.info("[{}] >>> {} {} | userId={} | ip={}",
            requestId,
            request.getMethod(),
            request.getPath(),
            userId != null ? userId : "anonymous",
            getClientIp(request)
        );

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            HttpStatusCode status = exchange.getResponse().getStatusCode();

            log.info("[{}] <<< {} {} | status={} | {}ms",
                requestId,
                request.getMethod(),
                request.getPath(),
                status != null ? status.value() : "unknown",
                duration
            );
        }));
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;  // Logging chạy đầu tiên
    }
}
