package com.example.sso.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * SSO 组件配置类
 * <p>
 * 自动扫描并注册 SSO 相关的组件（如 SSOService 实现类）
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(SSOConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.sso 包
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>JWT 密钥通过 application.yml 配置：jwt.secret</li>
 *   <li>Token 过期时间通过 application.yml 配置：jwt.expiration（秒）</li>
 *   <li>缓存服务可选，如果配置了缓存服务，可以实现 Token 黑名单功能</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.example.sso")
public class SSOConfig {
    
    /**
     * 配置类初始化
     */
    public SSOConfig() {
        log.info("SSO 组件配置已加载");
    }
}

