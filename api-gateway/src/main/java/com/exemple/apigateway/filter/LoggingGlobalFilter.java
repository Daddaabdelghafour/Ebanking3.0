package com.exemple.apigateway.filter;
// java

import com.exemple.apigateway.model.GatewayEvent;
import com.exemple.apigateway.model.RequestLog;
import com.exemple.apigateway.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    private final KafkaProducerService kafkaProducerService;

    public LoggingGlobalFilter(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        String path = request.getPath().value();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";

        logger.info("Incoming request: {} {}", method, path);

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String userId = extractUserId(securityContext);

                    sendGatewayEvent("REQUEST_RECEIVED", userId, path);

                    return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        logRequest(exchange, method, path, userId, startTime);
                    }));
                })
                .switchIfEmpty(
                        chain.filter(exchange).then(Mono.fromRunnable(() -> {
                            logRequest(exchange, method, path, "anonymous", startTime);
                        }))
                ).then();
    }

    private String extractUserId(SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();

            String userId = jwt.getClaimAsString("sub");
            if (userId == null) {
                userId = jwt.getClaimAsString("preferred_username");
            }
            return userId != null ? userId : "unknown";
        }

        return "anonymous";
    }

    private void logRequest(ServerWebExchange exchange, String method, String path,
                            String userId, long startTime) {

        ServerHttpResponse response = exchange.getResponse();
        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response.getStatusCode() != null ?
                response.getStatusCode().value() : 500;

        logger.info("Request completed: {} {} - Status: {} - Duration: {}ms - User: {}",
                method, path, statusCode, duration, userId);

        RequestLog requestLog = RequestLog.builder()
                .requestId(UUID.randomUUID().toString())
                .method(method)
                .path(path)
                .userId(userId)
                .statusCode(statusCode)
                .duration(duration)
                .timestamp(LocalDateTime.now())
                .userAgent(getUserAgent(exchange))
                .ipAddress(getClientIp(exchange))
                .build();

        kafkaProducerService.sendRequestLog(requestLog);

        sendGatewayEvent("REQUEST_COMPLETED", userId, path);
    }

    private void sendGatewayEvent(String eventType, String userId, String path) {
        GatewayEvent event = new GatewayEvent(
                eventType,
                userId,
                "api-gateway",
                String.format("%s:  %s", eventType, path)
        );

        kafkaProducerService.sendGatewayEvent(event);
    }

    private String getUserAgent(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("User-Agent");
    }

    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null) {
            ip = exchange.getRequest().getRemoteAddress() != null ?
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() :
                    "unknown";
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
