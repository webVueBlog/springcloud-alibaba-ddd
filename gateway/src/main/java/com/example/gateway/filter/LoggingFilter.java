package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 日志过滤器
 * <p>
 * 全局过滤器，用于记录所有经过网关的请求和响应信息
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>请求日志：记录请求方法、路径、客户端IP</li>
 *   <li>响应日志：记录响应状态码、请求耗时</li>
 *   <li>性能监控：计算请求处理时间</li>
 * </ul>
 * </p>
 * <p>
 * 执行顺序：-200（最早执行，在 AuthFilter 之前）
 * </p>
 * <p>
 * 日志格式：
 * <ul>
 *   <li>请求：网关请求: method={}, path={}, remoteAddress={}</li>
 *   <li>响应：网关响应: method={}, path={}, duration={}ms, status={}</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    /**
     * 过滤请求，记录请求和响应日志
     * 
     * @param exchange 请求交换对象
     * @param chain 过滤器链
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String remoteAddress = request.getRemoteAddress() != null 
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        log.info("网关请求: method={}, path={}, remoteAddress={}", method, path, remoteAddress);

        // 执行过滤器链，并在完成后记录响应日志
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            Integer statusCode = exchange.getResponse().getStatusCode() != null 
                    ? exchange.getResponse().getStatusCode().value() 
                    : null;
            log.info("网关响应: method={}, path={}, duration={}ms, status={}", 
                    method, path, duration, statusCode);
        }));
    }

    /**
     * 获取过滤器执行顺序
     * 
     * @return 执行顺序，-200 表示最早执行
     */
    @Override
    public int getOrder() {
        return -200;
    }
}

