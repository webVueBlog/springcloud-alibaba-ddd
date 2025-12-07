package com.example.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 内容服务启动类
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.content.infrastructure.mapper")
@ComponentScan(basePackages = {"com.example.content", "com.example.common"})
public class ContentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}

