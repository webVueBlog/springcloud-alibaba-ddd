package com.example.monitor.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 监控组件配置类
 * <p>
 * 配置 Spring Boot Actuator 监控功能，包括：
 * <ul>
 *   <li>健康检查端点</li>
 *   <li>指标收集端点</li>
 *   <li>监控服务 Bean</li>
 * </ul>
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(MonitorConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.monitor 包
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>需要在 application.yml 中配置 Actuator 端点暴露</li>
 *   <li>默认只暴露 health 和 metrics 端点</li>
 *   <li>可以通过 management.endpoints.web.exposure.include 配置暴露的端点</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.example.monitor")
public class MonitorConfig {
    
    // MonitorService 的实现类会自动注册为 Bean
    // HealthEndpoint 和 MetricsEndpoint 由 Spring Boot Actuator 自动配置
}

