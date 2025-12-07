package com.example.audit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 审计日志服务启动类
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.audit.infrastructure.mapper")
public class AuditApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }
}

