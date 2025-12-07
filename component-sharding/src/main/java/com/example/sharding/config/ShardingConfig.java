package com.example.sharding.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 分库分表配置类
 * <p>
 * 自动扫描并注册分片相关的组件（如 ShardingService、ShardingStrategy 实现类）
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(ShardingConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.sharding 包
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>此配置类默认启用，不依赖 ShardingSphere</li>
 *   <li>如果需要使用 ShardingSphere，需要添加依赖并配置</li>
 *   <li>ShardingSphere 配置通过 application.yml 进行配置</li>
 * </ul>
 * </p>
 * <p>
 * ShardingSphere 配置示例（可选）：
 * <pre>
 * spring:
 *   shardingsphere:
 *     enabled: true
 *     datasource:
 *       names: ds0,ds1
 *       ds0:
 *         driver-class-name: com.mysql.cj.jdbc.Driver
 *         url: jdbc:mysql://localhost:3306/db0
 *         username: root
 *         password: root
 *       ds1:
 *         driver-class-name: com.mysql.cj.jdbc.Driver
 *         url: jdbc:mysql://localhost:3306/db1
 *         username: root
 *         password: root
 *     rules:
 *       sharding:
 *         tables:
 *           t_order:
 *             actual-data-nodes: ds$->{0..1}.t_order_$->{0..1}
 *             table-strategy:
 *               standard:
 *                 sharding-column: order_id
 *                 sharding-algorithm-name: t_order_inline
 *         sharding-algorithms:
 *           t_order_inline:
 *             type: INLINE
 *             props:
 *               algorithm-expression: t_order_$->{order_id % 2}
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.example.sharding")
public class ShardingConfig {
    
    /**
     * 配置类初始化
     * <p>
     * 如果启用了 ShardingSphere，会记录日志
     * </p>
     */
    public ShardingConfig() {
        log.info("分库分表组件配置已加载");
    }
}

