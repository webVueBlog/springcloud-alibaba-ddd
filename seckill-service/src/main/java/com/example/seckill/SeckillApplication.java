package com.example.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
// 如果 Nacos 未启动，可以注释掉 @EnableDiscoveryClient
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// @EnableDiscoveryClient

/**
 * 秒杀服务启动类
 * <p>
 * 基于 DDD（领域驱动设计）架构的秒杀微服务，负责处理秒杀活动的创建、库存管理、秒杀下单等业务功能
 * </p>
 * <p>
 * 功能特性：
 * <ul>
 *   <li>秒杀下单：支持高并发的秒杀下单功能</li>
 *   <li>库存管理：使用 Redis 缓存管理秒杀库存</li>
 *   <li>分布式锁：使用分布式锁防止超卖</li>
 *   <li>限流保护：使用限流组件保护系统</li>
 *   <li>防重复下单：防止用户重复参与秒杀</li>
 * </ul>
 * </p>
 * <p>
 * 架构说明：
 * <ul>
 *   <li>领域层（domain）：秒杀活动领域模型和领域服务</li>
 *   <li>应用层（application）：应用服务和 DTO</li>
 *   <li>接口层（interfaces）：REST 控制器</li>
 * </ul>
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>扫描 seckill、cache、lock、ratelimit 包，加载相关组件</li>
 *   <li>Nacos 可选，如果未启动可以注释相关注解</li>
 *   <li>Redis 必需，用于库存管理和分布式锁</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.seckill", "com.example.cache", "com.example.lock", "com.example.ratelimit", "com.example.common"})
public class SeckillApplication {
    
    /**
     * 应用程序入口
     * 
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }
}

