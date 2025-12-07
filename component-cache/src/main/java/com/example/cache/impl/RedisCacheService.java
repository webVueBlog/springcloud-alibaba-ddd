package com.example.cache.impl;

import com.alibaba.fastjson2.JSON;
import com.example.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现
 * <p>
 * 基于 Redis 和 Spring Data Redis 实现的缓存服务
 * 使用 FastJSON2 进行对象序列化和反序列化
 * </p>
 * <p>
 * 注意：只有当 RedisTemplate 存在时才会创建此 Bean
 * 如果 Redis 未配置，此服务不会被创建，使用此服务的代码需要处理 null 的情况
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(RedisTemplate.class)
public class RedisCacheService implements CacheService {

    /** Redis 模板，用于操作 Redis */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存（不过期）
     * 
     * @param key 缓存键
     * @param value 缓存值
     */
    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("设置缓存成功: key={}", key);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 设置缓存（带过期时间）
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间数值
     * @param unit 过期时间单位
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("设置缓存成功: key={}, timeout={} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}, timeout={} {}", key, timeout, unit, e);
            throw e;
        }
    }

    /**
     * 获取缓存
     * <p>
     * 如果缓存的值类型与目标类型匹配，直接返回；否则使用 FastJSON2 进行转换
     * </p>
     * 
     * @param <T> 返回值的类型
     * @param key 缓存键
     * @param clazz 目标类型
     * @return 缓存的对象，如果不存在返回 null
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("缓存不存在: key={}", key);
                return null;
            }
            
            // 如果类型匹配，直接返回
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            
            // 否则使用 JSON 转换
            return JSON.parseObject(JSON.toJSONString(value), clazz);
        } catch (Exception e) {
            log.error("获取缓存失败: key={}", key, e);
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
            redisTemplate.delete(key);
            log.debug("删除缓存成功: key={}", key);
        } catch (Exception e) {
            log.error("删除缓存失败: key={}", key, e);
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
                redisTemplate.delete(keys);
                log.debug("批量删除缓存成功: count={}", keys.size());
            }
        } catch (Exception e) {
            log.error("批量删除缓存失败: keys={}", keys, e);
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
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断 key 是否存在失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 设置过期时间
     * 
     * @param key 缓存键
     * @param timeout 过期时间数值
     * @param unit 过期时间单位
     * @return true 表示设置成功，false 表示 key 不存在或设置失败
     */
    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("设置过期时间失败: key={}, timeout={} {}", key, timeout, unit, e);
            throw e;
        }
    }

    /**
     * 获取所有匹配的 key
     * <p>
     * 注意：在生产环境中，如果 key 数量很大，此操作可能很慢，建议谨慎使用
     * </p>
     * 
     * @param pattern 匹配模式，支持通配符 * 和 ?
     * @return 匹配的 key 集合
     */
    @Override
    public Set<String> keys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            log.debug("获取匹配的 key: pattern={}, count={}", pattern, keys != null ? keys.size() : 0);
            return keys;
        } catch (Exception e) {
            log.error("获取匹配的 key 失败: pattern={}", pattern, e);
            throw e;
        }
    }

    /**
     * 递增
     * 
     * @param key 缓存键
     * @return 递增后的值
     */
    @Override
    public Long increment(String key) {
        try {
            Long value = redisTemplate.opsForValue().increment(key);
            log.debug("递增成功: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("递增失败: key={}", key, e);
            throw e;
        }
    }

    /**
     * 递减
     * 
     * @param key 缓存键
     * @return 递减后的值
     */
    @Override
    public Long decrement(String key) {
        try {
            Long value = redisTemplate.opsForValue().decrement(key);
            log.debug("递减成功: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("递减失败: key={}", key, e);
            throw e;
        }
    }
}

