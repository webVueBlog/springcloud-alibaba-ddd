package com.example.statistics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 统计分析服务启动类
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.example.statistics.infrastructure.mapper")
public class StatisticsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }
}

