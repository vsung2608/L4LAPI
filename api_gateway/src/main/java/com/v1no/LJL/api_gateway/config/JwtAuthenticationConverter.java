package com.v1no.LJL.api_gateway.config;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.v1no.LJL.api_gateway.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER))
            .filter(header -> header.startsWith(BEARER_PREFIX))
            .map(header -> header.substring(BEARER_PREFIX.length()))
            .flatMap(token -> {
                if (!jwtUtil.isTokenValid(token)) {
                    log.warn("Invalid JWT token from: {}",
                        exchange.getRequest().getPath());
                    return Mono.empty();
                }

                String userId = jwtUtil.extractUserId(token);
                String role   = jwtUtil.extractRole(token);

                log.debug("JWT validated: userId={}, role={}", userId, role);

                List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, token, authorities);

                return Mono.just(authentication);
            });
    }
}
