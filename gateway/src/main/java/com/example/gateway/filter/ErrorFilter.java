package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局异常过滤器
 * <p>
 * 全局过滤器，用于捕获和处理网关中的异常
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>异常捕获：捕获过滤器链中的所有异常</li>
 *   <li>统一响应：返回统一的错误响应格式</li>
 *   <li>日志记录：记录异常信息，便于排查问题</li>
 * </ul>
 * </p>
 * <p>
 * 执行顺序：HIGHEST_PRECEDENCE（最高优先级，最先执行）
 * </p>
 * <p>
 * 响应格式：
 * <pre>
 * {
 *   "code": 500,
 *   "message": "错误消息",
 *   "timestamp": 1234567890
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class ErrorFilter implements GlobalFilter, Ordered {

    /**
     * 过滤请求，捕获异常并处理
     * 
     * @param exchange 请求交换对象
     * @param chain 过滤器链
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).onErrorResume(throwable -> {
            String path = exchange.getRequest().getURI().getPath();
            log.error("网关异常: path={}", path, throwable);
            return handleError(exchange, throwable);
        });
    }

    /**
     * 处理异常，返回错误响应
     * 
     * @param exchange 请求交换对象
     * @param throwable 异常对象
     * @return Mono<Void>
     */
    private Mono<Void> handleError(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // 获取错误消息，如果为空则使用默认消息
        String message = throwable.getMessage();
        if (!StringUtils.hasText(message)) {
            message = "服务器内部错误";
        }
        
        // 构建错误响应体
        String body = String.format("{\"code\":500,\"message\":\"%s\",\"timestamp\":%d}",
                escapeJson(message), System.currentTimeMillis());
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     * 
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * 获取过滤器执行顺序
     * 
     * @return 执行顺序，HIGHEST_PRECEDENCE 表示最高优先级
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

