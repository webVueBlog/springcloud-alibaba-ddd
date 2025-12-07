package com.example.gateway.filter;

import com.example.sso.SSOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 认证过滤器
 * <p>
 * 全局过滤器，用于验证请求的 JWT Token 并提取用户信息
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>白名单检查：白名单路径直接放行，无需认证</li>
 *   <li>Token 提取：从请求头 Authorization 中提取 Bearer Token</li>
 *   <li>Token 验证：使用 SSO 服务验证 Token 的有效性</li>
 *   <li>用户信息传递：将用户ID和 Token 添加到请求头，传递给下游服务</li>
 * </ul>
 * </p>
 * <p>
 * 执行顺序：-100（在 LoggingFilter 之后执行）
 * </p>
 * <p>
 * 请求头说明：
 * <ul>
 *   <li>输入：Authorization: Bearer {token}</li>
 *   <li>输出：X-User-Id: {userId}, X-Token: {token}</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    /** SSO 服务（可选，如果未配置则跳过认证） */
    @Autowired(required = false)
    private SSOService ssoService;

    /** 白名单路径，这些路径不需要认证即可访问 */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",           // 登录接口
            "/api/auth/verify-code",     // 验证码接口
            "/api/auth/register",        // 注册接口
            "/actuator",                 // Actuator 端点
            "/actuator/health",          // 健康检查
            "/actuator/info"             // 应用信息
    );

    /**
     * 过滤请求，进行认证验证
     * 
     * @param exchange 请求交换对象
     * @param chain 过滤器链
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单直接放行
        if (isWhiteList(path)) {
            log.debug("白名单路径，跳过认证: path={}", path);
            return chain.filter(exchange);
        }

        // 如果 SSO 服务未配置，跳过认证
        if (ssoService == null) {
            log.warn("SSO 服务未配置，跳过认证: path={}", path);
            return chain.filter(exchange);
        }

        // 获取 Token
        String token = getToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("请求未提供 Token: path={}", path);
            return unauthorized(exchange.getResponse(), "未提供认证Token");
        }

        // 验证 Token
        try {
            if (!ssoService.validateToken(token)) {
                log.warn("Token 验证失败: path={}", path);
                return unauthorized(exchange.getResponse(), "Token无效或已过期");
            }

            // 从 Token 中提取用户ID
            Long userId = ssoService.getUserIdFromToken(token);
            log.debug("Token 验证成功: path={}, userId={}", path, userId);

            // 将用户信息添加到请求头，传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Token", token)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.error("Token 验证异常: path={}", path, e);
            return unauthorized(exchange.getResponse(), "Token验证失败: " + e.getMessage());
        }
    }

    /**
     * 检查路径是否在白名单中
     * 
     * @param path 请求路径
     * @return true 表示在白名单中，false 表示不在
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 从请求头中获取 Token
     * <p>
     * 支持格式：Authorization: Bearer {token}
     * </p>
     * 
     * @param request HTTP 请求
     * @return Token 字符串，如果未找到返回 null
     */
    private String getToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    /**
     * 返回未授权响应
     * 
     * @param response HTTP 响应
     * @param message 错误消息
     * @return Mono<Void>
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        String body = String.format("{\"code\":401,\"message\":\"%s\",\"timestamp\":%d}", 
                message, System.currentTimeMillis());
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 获取过滤器执行顺序
     * 
     * @return 执行顺序，-100 表示在 LoggingFilter 之后执行
     */
    @Override
    public int getOrder() {
        return -100;
    }
}

