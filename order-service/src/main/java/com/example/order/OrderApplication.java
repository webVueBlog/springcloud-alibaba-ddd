package com.example.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单服务启动类
 * <p>
 * 基于 DDD（领域驱动设计）架构的订单微服务，负责处理订单的创建、查询、状态更新等业务功能
 * </p>
 * <p>
 * 功能特性：
 * <ul>
 *   <li>订单管理：创建订单、查询订单、更新订单状态</li>
 *   <li>分布式事务：使用 Seata AT 模式保证数据一致性</li>
 *   <li>消息队列：订单创建后自动发送消息到 RocketMQ</li>
 *   <li>服务发现：支持 Nacos 服务注册与发现（可选）</li>
 * </ul>
 * </p>
 * <p>
 * 架构说明：
 * <ul>
 *   <li>领域层（domain）：订单领域模型和仓储接口</li>
 *   <li>应用层（application）：应用服务和 DTO</li>
 *   <li>基础设施层（infrastructure）：仓储实现、Mapper、PO</li>
 *   <li>接口层（interfaces）：REST 控制器</li>
 * </ul>
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>扫描 order、message、transaction 包，加载相关组件</li>
 *   <li>扫描 infrastructure.mapper 包，加载 MyBatis Mapper</li>
 *   <li>Nacos 和 Seata 可选，如果未启动可以注释相关注解</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
// @EnableDiscoveryClient  // 如果 Nacos 未启动，可以注释掉
// @EnableDistributedTransaction  // 如果 Seata 未启动，可以注释掉
@MapperScan("com.example.order.infrastructure.mapper")
@ComponentScan(basePackages = {"com.example.order", "com.example.message", "com.example.transaction", "com.example.common"})
public class OrderApplication {
    
    /**
     * 应用程序入口
     * 
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

