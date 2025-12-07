package com.example.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 网关配置类
 * <p>
 * 配置网关相关的 Bean，包括限流 Key 解析器等
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>IP 限流：按客户端 IP 地址进行限流</li>
 *   <li>用户限流：按用户ID进行限流</li>
 * </ul>
 * </p>
 * <p>
 * 使用说明：
 * <ul>
 *   <li>这些 KeyResolver 可以用于 Spring Cloud Gateway 的限流功能</li>
 *   <li>需要在路由配置中使用 RequestRateLimiter 过滤器</li>
 *   <li>需要配置 Redis 作为限流存储（如果使用 RedisRateLimiter）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class GatewayConfig {

    /**
     * IP 限流 Key 解析器
     * <p>
     * 根据客户端 IP 地址生成限流 Key
     * 适用于按 IP 地址进行限流的场景
     * </p>
     * <p>
     * 使用示例（在路由配置中）：
     * <pre>
     * filters:
     *   - name: RequestRateLimiter
     *     args:
     *       key-resolver: "#{@ipKeyResolver}"
     *       redis-rate-limiter.replenishRate: 10
     *       redis-rate-limiter.burstCapacity: 20
     * </pre>
     * </p>
     * 
     * @return IP 限流 Key 解析器
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null 
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            log.debug("IP 限流 Key: {}", ip);
            return Mono.just(ip);
        };
    }

    /**
     * 用户限流 Key 解析器
     * <p>
     * 根据用户ID生成限流 Key
     * 适用于按用户进行限流的场景
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>需要 AuthFilter 在请求头中设置 X-User-Id</li>
     *   <li>如果未设置 X-User-Id，则使用 "anonymous" 作为 Key</li>
     * </ul>
     * </p>
     * <p>
     * 使用示例（在路由配置中）：
     * <pre>
     * filters:
     *   - name: RequestRateLimiter
     *     args:
     *       key-resolver: "#{@userKeyResolver}"
     *       redis-rate-limiter.replenishRate: 5
     *       redis-rate-limiter.burstCapacity: 10
     * </pre>
     * </p>
     * 
     * @return 用户限流 Key 解析器
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String key = userId != null ? userId : "anonymous";
            log.debug("用户限流 Key: {}", key);
            return Mono.just(key);
        };
    }
}

