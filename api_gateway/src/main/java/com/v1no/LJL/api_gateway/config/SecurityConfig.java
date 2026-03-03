package com.v1no.LJL.api_gateway.config;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtFilter = buildJwtFilter();

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)

            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/refresh-token"
                ).permitAll()

                .pathMatchers(HttpMethod.GET, EndPoints.PUBLIC_GET_API).permitAll()

                .pathMatchers("/internal/**").denyAll()

                .pathMatchers(HttpMethod.POST, EndPoints.ADMIN_POST_API).hasRole("ADMIN")
                
                .pathMatchers(HttpMethod.PUT, EndPoints.ADMIN_PUT_API).hasRole("ADMIN")
                
                .pathMatchers(HttpMethod.DELETE, EndPoints.ADMIN_DELETE_API).hasRole("ADMIN")

                .anyExchange().authenticated()
            )

            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)

            .exceptionHandling(handling -> handling
                .authenticationEntryPoint(unauthorizedEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )

            .build();
    }

    private AuthenticationWebFilter buildJwtFilter() {
        AuthenticationWebFilter filter =
            new AuthenticationWebFilter(jwtAuthenticationManager);

        filter.setServerAuthenticationConverter(jwtAuthenticationConverter);

        // Không redirect về login page — trả 401 JSON
        filter.setAuthenticationFailureHandler(
            (exchange, exception) -> onAuthenticationFailure(exchange.getExchange(), exception)
        );

        return filter;
    }

    private ServerAuthenticationEntryPoint unauthorizedEntryPoint() {
        return (exchange, exception) -> onAuthenticationFailure(exchange, exception);
    }

    private ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, exception) -> writeErrorResponse(
            exchange,
            HttpStatus.FORBIDDEN,
            "Access denied — insufficient permissions"
        );
    }

    private Mono<Void> onAuthenticationFailure(
        ServerWebExchange exchange,
        AuthenticationException exception
    ) {
        return writeErrorResponse(
            exchange,
            HttpStatus.UNAUTHORIZED,
            "Unauthorized — " + exception.getMessage()
        );
    }

    private Mono<Void> writeErrorResponse(
        ServerWebExchange exchange,
        HttpStatus status,
        String message
    ) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
              "status": %d,
              "message": "%s",
              "timestamp": "%s"
            }
            """.formatted(status.value(), message, LocalDateTime.now());

        DataBuffer buffer = response.bufferFactory()
            .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
