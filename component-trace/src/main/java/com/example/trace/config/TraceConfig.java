package com.example.trace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式追踪配置类
 * <p>
 * 自动扫描并注册追踪相关的组件（如 TraceService 实现类）
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(TraceConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.trace 包
 * </p>
 * <p>
 * 实现说明：
 * <ul>
 *   <li>默认使用 MDCTraceService（基于 MDC 的简单实现）</li>
 *   <li>如果检测到 Sleuth，会使用 SleuthTraceService</li>
 *   <li>可以通过配置选择使用哪个实现</li>
 * </ul>
 * </p>
 * <p>
 * Sleuth 集成（可选）：
 * <p>
 * 如果需要使用 Sleuth，需要添加以下依赖：
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
 *     &lt;artifactId&gt;spring-cloud-starter-sleuth&lt;/artifactId&gt;
 * &lt;/dependency&gt;
 * </pre>
 * </p>
 * <p>
 * 在 application.yml 中配置：
 * <pre>
 * spring:
 *   sleuth:
 *     enabled: true
 * </pre>
 * </p>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.example.trace")
public class TraceConfig {
    
    /**
     * 配置类初始化
     */
    public TraceConfig() {
        log.info("分布式追踪组件配置已加载");
    }
}

