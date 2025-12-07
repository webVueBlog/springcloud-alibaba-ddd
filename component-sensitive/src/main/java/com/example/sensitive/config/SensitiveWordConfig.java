package com.example.sensitive.config;

import com.example.sensitive.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 敏感词组件配置类
 * <p>
 * 自动扫描并注册敏感词相关的组件（如 SensitiveWordService 实现类）
 * 在应用启动时自动初始化默认敏感词库
 * </p>
 * <p>
 * 使用此组件时，需要在启动类或配置类中导入此配置：
 * <pre>
 * @SpringBootApplication
 * @Import(SensitiveWordConfig.class)
 * public class Application {
 *     // ...
 * }
 * </pre>
 * 或者确保 Spring Boot 能够扫描到 com.example.sensitive 包
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>应用启动时会自动初始化默认敏感词库</li>
 *   <li>可以通过配置文件或数据库加载敏感词</li>
 *   <li>支持动态更新敏感词库</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.example.sensitive")
public class SensitiveWordConfig implements CommandLineRunner {

    /** 敏感词服务 */
    @Autowired(required = false)
    private SensitiveWordService sensitiveWordService;

    /**
     * 应用启动时自动初始化敏感词库
     * 
     * @param args 启动参数
     */
    @Override
    public void run(String... args) {
        if (sensitiveWordService != null) {
            try {
                // 初始化默认敏感词库
                List<String> defaultWords = getDefaultSensitiveWords();
                sensitiveWordService.initSensitiveWords(defaultWords);
                log.info("敏感词库初始化完成: count={}", defaultWords.size());
            } catch (Exception e) {
                log.error("敏感词库初始化失败", e);
            }
        } else {
            log.warn("SensitiveWordService 未找到，跳过敏感词库初始化");
        }
    }

    /**
     * 获取默认敏感词列表
     * <p>
     * 实际项目中应该从配置文件、数据库或文件加载敏感词
     * </p>
     * 
     * @return 默认敏感词列表
     */
    private List<String> getDefaultSensitiveWords() {
        // 默认敏感词列表（示例）
        // 实际项目中应该从配置文件或数据库加载
        List<String> words = new ArrayList<>();
        
        // 可以从配置文件加载
        // words.addAll(loadFromProperties());
        
        // 可以从数据库加载
        // words.addAll(loadFromDatabase());
        
        // 可以从文件加载
        // words.addAll(loadFromFile());
        
        // 示例敏感词（实际使用时应该移除或替换）
        words.addAll(Arrays.asList(
            "敏感词1",
            "敏感词2",
            "测试敏感词"
        ));
        
        return words;
    }
}

