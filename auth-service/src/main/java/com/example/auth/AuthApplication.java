package com.example.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 认证服务启动类
 * <p>
 * 提供用户认证、授权、登录等核心功能，支持多种登录方式：
 * <ul>
 *   <li>用户名密码登录</li>
 *   <li>手机号密码登录</li>
 *   <li>手机号验证码登录</li>
 *   <li>微信登录（PC端）</li>
 *   <li>小程序微信登录</li>
 * </ul>
 * </p>
 * <p>
 * 依赖组件：
 * <ul>
 *   <li>component-sso: 单点登录和Token管理</li>
 *   <li>component-encrypt: 密码加密</li>
 *   <li>component-cache: 验证码缓存（可选）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class,  // 如果 Redis 未启动，排除 Redis 自动配置
    RedissonAutoConfiguration.class  // 如果 Redis 未启动，排除 Redisson 自动配置
})
@MapperScan("com.example.auth.infrastructure.mapper")
@ComponentScan(basePackages = {"com.example.auth", "com.example.encrypt", "com.example.sso", "com.example.cache", "com.example.common"})
public class AuthApplication {
    
    /**
     * 应用启动入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}

