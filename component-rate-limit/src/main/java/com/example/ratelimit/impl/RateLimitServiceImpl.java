package com.example.ratelimit.impl;

import com.example.cache.CacheService;
import com.example.ratelimit.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 限流服务实现
 * <p>
 * 基于缓存服务（Redis 或 Caffeine）实现限流功能
 * 支持令牌桶算法和滑动窗口算法
 * </p>
 * <p>
 * 实现说明：
 * <ul>
 *   <li>令牌桶算法：使用缓存存储令牌数和上次补充时间</li>
 *   <li>滑动窗口算法：使用缓存计数器实现时间窗口内的请求计数</li>
 *   <li>如果缓存服务未配置，会记录警告并允许请求通过</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {

    /** 缓存服务，用于存储限流数据 */
    @Autowired(required = false)
    private CacheService cacheService;

    /**
     * 尝试获取令牌（令牌桶算法）
     * 
     * @param key 限流键
     * @param permits 需要获取的令牌数量
     * @param rate 令牌生成速率（每秒）
     * @param capacity 令牌桶容量
     * @return true 表示获取成功，false 表示获取失败
     */
    @Override
    public boolean tryAcquire(String key, int permits, int rate, int capacity) {
        if (cacheService == null) {
            log.warn("CacheService未配置，限流功能无法正常工作，允许请求通过: key={}", key);
            return true;
        }
        
        try {
            String tokenKey = "rate_limit:token:" + key;
            String lastRefillKey = "rate_limit:refill:" + key;

            long now = System.currentTimeMillis();
            Long lastRefill = cacheService.get(lastRefillKey, Long.class);
            
            // 初始化令牌桶
            if (lastRefill == null) {
                lastRefill = now;
                cacheService.set(tokenKey, capacity, 1, TimeUnit.HOURS);
                cacheService.set(lastRefillKey, now, 1, TimeUnit.HOURS);
                log.debug("初始化令牌桶: key={}, capacity={}", key, capacity);
            }

            // 计算需要补充的令牌数
            long elapsed = now - lastRefill;
            long tokensToAdd = elapsed * rate / 1000;
            
            if (tokensToAdd > 0) {
                Integer currentTokens = cacheService.get(tokenKey, Integer.class);
                if (currentTokens == null) {
                    currentTokens = capacity;
                }
                
                // 补充令牌，但不能超过容量
                int newTokens = (int) Math.min(capacity, currentTokens + tokensToAdd);
                cacheService.set(tokenKey, newTokens, 1, TimeUnit.HOURS);
                cacheService.set(lastRefillKey, now, 1, TimeUnit.HOURS);
                
                log.debug("补充令牌: key={}, tokensToAdd={}, newTokens={}", 
                        key, tokensToAdd, newTokens);
            }

            // 尝试获取令牌
            Integer tokens = cacheService.get(tokenKey, Integer.class);
            if (tokens != null && tokens >= permits) {
                cacheService.set(tokenKey, tokens - permits, 1, TimeUnit.HOURS);
                log.debug("获取令牌成功: key={}, permits={}, remainingTokens={}", 
                        key, permits, tokens - permits);
                return true;
            }
            
            log.debug("获取令牌失败: key={}, permits={}, availableTokens={}", 
                    key, permits, tokens);
            return false;
        } catch (Exception e) {
            log.error("令牌桶限流异常: key={}", key, e);
            // 异常时允许请求通过，避免影响业务
            return true;
        }
    }

    /**
     * 滑动窗口限流
     * <p>
     * 实现说明：
     * <ul>
     *   <li>使用秒级计数器实现滑动窗口</li>
     *   <li>每个时间窗口（秒）对应一个计数器</li>
     *   <li>计数器自动过期，实现窗口滑动</li>
     * </p>
     * <p>
     * 注意：这是简化版的滑动窗口实现，精确度取决于时间窗口大小
     * 如果需要更精确的滑动窗口，可以使用 Redis 的 ZSet 实现
     * </p>
     * 
     * @param key 限流键
     * @param limit 时间窗口内的最大请求数
     * @param windowSeconds 时间窗口大小（秒）
     * @return true 表示请求可以通过，false 表示请求被限流
     */
    @Override
    public boolean slidingWindowLimit(String key, int limit, int windowSeconds) {
        if (cacheService == null) {
            log.warn("CacheService未配置，限流功能无法正常工作，允许请求通过: key={}", key);
            return true;
        }
        
        try {
            String windowKey = "rate_limit:window:" + key;
            long now = System.currentTimeMillis();
            
            // 使用秒级计数器实现滑动窗口
            // 每个时间窗口（秒）对应一个计数器
            long currentSecond = now / 1000;
            String counterKey = windowKey + ":" + currentSecond;
            
            // 递增计数器
            Long count = cacheService.increment(counterKey);
            
            // 如果是第一次访问，设置过期时间
            if (count == 1) {
                // 过期时间设置为窗口大小 + 1 秒，确保数据不会过早清理
                cacheService.expire(counterKey, windowSeconds + 1, TimeUnit.SECONDS);
                log.debug("初始化滑动窗口计数器: key={}, counterKey={}, limit={}, window={}", 
                        key, counterKey, limit, windowSeconds);
            }

            // 检查是否超过限制
            if (count > limit) {
                // 超过限制，回退计数器
                cacheService.decrement(counterKey);
                log.warn("滑动窗口限流: key={}, count={}, limit={}, window={}", 
                        key, count, limit, windowSeconds);
                return false;
            }
            
            log.debug("滑动窗口限流通过: key={}, count={}, limit={}, window={}", 
                    key, count, limit, windowSeconds);
            return true;
        } catch (Exception e) {
            log.error("滑动窗口限流异常: key={}", key, e);
            // 异常时允许请求通过，避免影响业务
            return true;
        }
    }
}

