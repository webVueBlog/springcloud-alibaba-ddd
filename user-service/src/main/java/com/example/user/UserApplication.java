package com.example.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 * <p>
 * 基于 DDD（领域驱动设计）架构的用户微服务，负责处理用户角色、权限管理等业务功能
 * </p>
 * <p>
 * 功能特性：
 * <ul>
 *   <li>角色管理：查询用户角色、角色权限关联</li>
 *   <li>权限管理：查询用户权限、权限验证</li>
 *   <li>用户角色关联：管理用户和角色的多对多关系</li>
 *   <li>角色权限关联：管理角色和权限的多对多关系</li>
 * </ul>
 * </p>
 * <p>
 * 架构说明：
 * <ul>
 *   <li>领域层（domain）：角色、权限领域模型</li>
 *   <li>应用层（application）：应用服务和 DTO</li>
 *   <li>基础设施层（infrastructure）：仓储实现、Mapper、PO</li>
 *   <li>接口层（interfaces）：REST 控制器</li>
 * </ul>
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>扫描 user、cache、common 包，加载相关组件</li>
 *   <li>扫描 infrastructure.mapper 包，加载 MyBatis Mapper</li>
 *   <li>Nacos 可选，如果未启动可以注释相关注解</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
// @EnableDiscoveryClient  // 如果 Nacos 未启动，可以注释掉
@MapperScan("com.example.user.infrastructure.mapper")
@ComponentScan(basePackages = {"com.example.user", "com.example.cache", "com.example.common"})
public class UserApplication {
    
    /**
     * 应用程序入口
     * 
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

