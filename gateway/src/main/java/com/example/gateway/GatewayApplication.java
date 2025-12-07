package com.example.gateway;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 网关服务启动类
 * <p>
 * 基于 Spring Cloud Gateway 的 API 网关服务，提供统一入口、路由转发、认证授权等功能
 * </p>
 * <p>
 * 功能特性：
 * <ul>
 *   <li>路由转发：支持动态路由配置和负载均衡</li>
 *   <li>认证授权：基于 JWT Token 的统一认证</li>
 *   <li>请求日志：记录所有请求和响应</li>
 *   <li>异常处理：全局异常捕获和统一响应</li>
 *   <li>跨域支持：支持 CORS 跨域请求</li>
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>排除 Spring MVC 自动配置，使用响应式 Web 应用类型</li>
 *   <li>Redis 和 Redisson 自动配置可选，如果未启动可以排除</li>
 *   <li>扫描 gateway、sso、cache 包，加载相关组件</li>
 * </ul>
 * </p>
 * <p>
 * 启动要求：
 * <ul>
 *   <li>Nacos（可选）：服务注册与发现，如果未启动需在配置中禁用</li>
 *   <li>Redis（可选）：SSO Token 存储，如果未启动需在配置中禁用</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class,  // 如果 Redis 未启动，排除 Redis 自动配置
    RedissonAutoConfiguration.class,  // 如果 Redis 未启动，排除 Redisson 自动配置
    DispatcherServletAutoConfiguration.class,  // 排除 Spring MVC DispatcherServlet 自动配置
    ServletWebServerFactoryAutoConfiguration.class,  // 排除 Servlet Web 服务器自动配置
    WebMvcAutoConfiguration.class  // 排除 Spring MVC 自动配置
})
// @EnableDiscoveryClient  // 如果 Nacos 未启动，可以注释掉
@ComponentScan(basePackages = {"com.example.gateway", "com.example.sso", "com.example.cache"})
public class GatewayApplication {
    
    /**
     * 应用程序入口
     * 
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

