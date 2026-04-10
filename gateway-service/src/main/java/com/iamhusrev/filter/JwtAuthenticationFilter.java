package com.iamhusrev.filter;

import com.iamhusrev.security.JwtUtil;
import com.iamhusrev.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> OPEN_PATHS = List.of(
            "/iamhusrev/dev/auth/",
            "/api/auth/",
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isOpenPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());

        try {
            Claims claims = jwtUtil.validateToken(token);

            Long userId = jwtUtil.getUserId(claims);
            String userName = jwtUtil.getUserName(claims);
            Long organizationId = jwtUtil.getOrganizationId(claims);
            String role = jwtUtil.getRole(claims);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(SecurityConstants.HEADER_USER_ID, String.valueOf(userId))
                    .header(SecurityConstants.HEADER_USER_NAME, userName)
                    .header(SecurityConstants.HEADER_ORGANIZATION_ID, String.valueOf(organizationId))
                    .header(SecurityConstants.HEADER_USER_ROLE, role)
                    .headers(h -> h.remove(HttpHeaders.AUTHORIZATION))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isOpenPath(String path) {
        return OPEN_PATHS.stream().anyMatch(path::contains);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"success\":false,\"message\":\"" + message + "\",\"code\":" + status.value() + "}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
