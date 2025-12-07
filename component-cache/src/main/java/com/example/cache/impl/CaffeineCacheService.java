package com.example.cache.impl;

import com.example.cache.CacheService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Caffeine 本地缓存服务实现
 * <p>
 * 基于 Caffeine 实现的本地缓存服务，提供高性能的本地缓存功能
 * 适用于单机环境或需要快速访问的热点数据
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>高性能：基于内存的本地缓存，访问速度快</li>
 *   <li>自动过期：支持基于时间和基于大小的过期策略</li>
 *   <li>自动淘汰：使用 LRU 策略自动淘汰过期或最少使用的数据</li>
 *   <li>线程安全：支持并发访问</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>本地缓存只存在于当前 JVM 实例中，不适用于分布式环境</li>
 *   <li>不支持跨进程共享数据</li>
 *   <li>keys() 方法使用正则表达式匹配，性能可能不如 Redis</li>
 *   <li>increment() 和 decrement() 方法使用 ConcurrentHashMap 实现</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "cache.local.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnBean(Cache.class)
public class CaffeineCacheService implements CacheService {

    /** Caffeine 缓存实例 */
    private final Cache<String, Object> cache;
    
    /** 计数器缓存（用于 increment 和 decrement） */
    private final ConcurrentHashMap<String, Long> counters = new ConcurrentHashMap<>();

    /**
     * 设置缓存（不过期）
     * <p>
     * 注意：本地缓存仍然会受最大大小限制，可能会被 LRU 策略淘汰
     * </p>
     * 
     * @param key 缓存键
     * @param value 缓存值
     */
    @Override
    public void set(String key, Object value) {
        try {
            cache.put(key, value);
            log.debug("设置本地缓存成功: key={}", key);
        } catch (Exception e) {
            log.error("设置本地缓存失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 设置缓存（带过期时间）
     * <p>
     * 注意：Caffeine 不支持为单个 key 设置不同的过期时间
     * 此方法会设置缓存，但过期时间由全局配置决定
     * </p>
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间数值（会被忽略，使用全局配置）
     * @param unit 过期时间单位（会被忽略，使用全局配置）
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            // Caffeine 不支持为单个 key 设置过期时间，使用全局配置
            cache.put(key, value);
            log.debug("设置本地缓存成功: key={}, timeout={} {} (使用全局过期时间)", key, timeout, unit);
        } catch (Exception e) {
            log.error("设置本地缓存失败: key={}, timeout={} {}", key, timeout, unit, e);
            throw e;
        }
    }

    /**
     * 获取缓存
     * 
     * @param <T> 返回值的类型
     * @param key 缓存键
     * @param clazz 目标类型
     * @return 缓存的对象，如果不存在返回 null
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = cache.getIfPresent(key);
            if (value == null) {
                log.debug("本地缓存不存在: key={}", key);
                return null;
            }
            
            // 类型转换
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            
            log.warn("缓存值类型不匹配: key={}, expected={}, actual={}", 
                    key, clazz.getName(), value.getClass().getName());
            return null;
        } catch (Exception e) {
            log.error("获取本地缓存失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 缓存键
     */
    @Override
    public void delete(String key) {
        try {
            cache.invalidate(key);
            counters.remove(key);  // 同时删除计数器
            log.debug("删除本地缓存成功: key={}", key);
        } catch (Exception e) {
            log.error("删除本地缓存失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 批量删除缓存
     * 
     * @param keys 缓存键列表
     */
    @Override
    public void deleteBatch(List<String> keys) {
        try {
            if (keys != null && !keys.isEmpty()) {
                cache.invalidateAll(keys);
                keys.forEach(counters::remove);  // 同时删除计数器
                log.debug("批量删除本地缓存成功: count={}", keys.size());
            }
        } catch (Exception e) {
            log.error("批量删除本地缓存失败: keys={}", keys, e);
            throw e;
        }
    }

    /**
     * 判断 key 是否存在
     * 
     * @param key 缓存键
     * @return true 表示 key 存在，false 表示不存在
     */
    @Override
    public boolean exists(String key) {
        try {
            boolean exists = cache.getIfPresent(key) != null;
            log.debug("判断 key 是否存在: key={}, exists={}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("判断 key 是否存在失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 设置过期时间
     * <p>
     * 注意：Caffeine 不支持为单个 key 设置过期时间
     * 此方法会返回 false，因为无法为已存在的 key 设置过期时间
     * </p>
     * 
     * @param key 缓存键
     * @param timeout 过期时间数值
     * @param unit 过期时间单位
     * @return false（Caffeine 不支持此操作）
     */
    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        log.warn("Caffeine 不支持为单个 key 设置过期时间: key={}, timeout={} {}", key, timeout, unit);
        return false;
    }

    /**
     * 获取所有匹配的 key
     * <p>
     * 使用正则表达式匹配，将通配符模式转换为正则表达式
     * 注意：此操作需要遍历所有 key，性能可能不如 Redis
     * </p>
     * 
     * @param pattern 匹配模式，支持通配符 * 和 ?
     * @return 匹配的 key 集合
     */
    @Override
    public Set<String> keys(String pattern) {
        try {
            // 将通配符模式转换为正则表达式
            String regex = pattern
                    .replace(".", "\\.")
                    .replace("*", ".*")
                    .replace("?", ".");
            Pattern compiledPattern = Pattern.compile(regex);
            
            // 遍历所有 key 进行匹配
            Set<String> matchedKeys = cache.asMap().keySet().stream()
                    .filter(key -> compiledPattern.matcher(key).matches())
                    .collect(Collectors.toSet());
            
            log.debug("获取匹配的 key: pattern={}, count={}", pattern, matchedKeys.size());
            return matchedKeys;
        } catch (Exception e) {
            log.error("获取匹配的 key 失败: pattern={}", pattern, e);
            throw e;
        }
    }

    /**
     * 递增
     * <p>
     * 使用 ConcurrentHashMap 实现计数器功能
     * </p>
     * 
     * @param key 缓存键
     * @return 递增后的值
     */
    @Override
    public Long increment(String key) {
        try {
            Long value = counters.compute(key, (k, v) -> (v == null ? 0L : v) + 1);
            log.debug("递增成功: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("递增失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 递减
     * <p>
     * 使用 ConcurrentHashMap 实现计数器功能
     * </p>
     * 
     * @param key 缓存键
     * @return 递减后的值
     */
    @Override
    public Long decrement(String key) {
        try {
            Long value = counters.compute(key, (k, v) -> (v == null ? 0L : v) - 1);
            log.debug("递减成功: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("递减失败: key={}", key, e);
            throw e;
        }
    }
}

