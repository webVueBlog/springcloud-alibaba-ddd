package com.example.search.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 搜索组件配置类
 * <p>
 * 自动扫描并注册搜索相关的组件（如 SearchService 实现类）
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(SearchConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.search 包
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>需要配置 Elasticsearch 连接信息</li>
 *   <li>需要在 application.yml 中配置 spring.elasticsearch.uris</li>
 *   <li>如果 Elasticsearch 未配置，搜索服务不会被创建</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.example.search")
public class SearchConfig {
}

