package com.example.ratelimit.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 限流组件配置类
 * <p>
 * 自动扫描并注册限流相关的组件（如 RateLimitService、RateLimitAspect 等）
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(RateLimitConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.ratelimit 包
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>需要依赖 component-cache 组件（Redis 或 Caffeine）</li>
 *   <li>如果缓存服务未配置，限流功能会失效（允许所有请求通过）</li>
 *   <li>需要启用 AOP 功能（Spring Boot 默认启用）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.example.ratelimit")
public class RateLimitConfig {
}

