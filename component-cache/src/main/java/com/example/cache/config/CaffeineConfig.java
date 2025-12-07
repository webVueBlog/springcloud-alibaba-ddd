package com.example.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置类
 * <p>
 * 配置 Caffeine 本地缓存，提供高性能的本地缓存功能
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>只有当 cache.local.enabled=true 时才会生效（默认不启用）</li>
 *   <li>支持配置最大缓存大小和过期时间</li>
 *   <li>使用 LRU 策略自动淘汰过期或最少使用的数据</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "cache.local.enabled", havingValue = "true", matchIfMissing = false)
public class CaffeineConfig {

    /** 缓存最大大小，默认 10000 */
    @Value("${cache.local.max-size:10000}")
    private long maxSize;

    /** 过期时间（秒），默认 3600 秒（1小时） */
    @Value("${cache.local.expire-after-write:3600}")
    private long expireAfterWrite;

    /**
     * 创建 Caffeine 缓存实例
     * <p>
     * 配置说明：
     * <ul>
     *   <li>maximumSize: 最大缓存条目数，超过后会使用 LRU 策略淘汰</li>
     *   <li>expireAfterWrite: 写入后过期时间，超过时间后自动删除</li>
     *   <li>recordStats: 启用统计功能，可以查看缓存命中率等指标</li>
     * </ul>
     * </p>
     * 
     * @return Caffeine 缓存实例
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        Cache<String, Object> cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS)
                .recordStats()  // 启用统计功能
                .build();
        
        log.info("Caffeine 本地缓存配置成功: maxSize={}, expireAfterWrite={}s", 
                maxSize, expireAfterWrite);
        
        return cache;
    }
}

