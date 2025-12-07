package com.example.cache;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 * <p>
 * 提供统一的缓存操作接口，支持：
 * <ul>
 *   <li>设置和获取缓存</li>
 *   <li>设置过期时间</li>
 *   <li>删除缓存（单个和批量）</li>
 *   <li>判断 key 是否存在</li>
 *   <li>获取匹配的 key</li>
 *   <li>数值的递增和递减</li>
 * </ul>
 * </p>
 * <p>
 * 当前实现基于 Redis，使用 FastJSON2 进行序列化
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入缓存服务
 * @Autowired
 * private CacheService cacheService;
 * 
 * // 设置缓存
 * cacheService.set("user:1", user);
 * 
 * // 设置缓存（带过期时间）
 * cacheService.set("token:123", token, 30, TimeUnit.MINUTES);
 * 
 * // 获取缓存
 * User user = cacheService.get("user:1", User.class);
 * 
 * // 删除缓存
 * cacheService.delete("user:1");
 * 
 * // 判断 key 是否存在
 * if (cacheService.exists("user:1")) {
 *     // key 存在
 * }
 * 
 * // 递增
 * Long count = cacheService.increment("counter:1");
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CacheService {
    
    /**
     * 设置缓存
     * <p>
     * 将对象存储到缓存中，不过期
     * </p>
     * 
     * @param key 缓存键，建议使用命名空间格式，如 "user:1"、"token:123"
     * @param value 缓存值，可以是任意对象
     */
    void set(String key, Object value);

    /**
     * 设置缓存（带过期时间）
     * <p>
     * 将对象存储到缓存中，并设置过期时间，过期后自动删除
     * </p>
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间数值
     * @param unit 过期时间单位（如 TimeUnit.SECONDS、TimeUnit.MINUTES、TimeUnit.HOURS 等）
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取缓存
     * <p>
     * 从缓存中获取对象，如果 key 不存在或已过期，返回 null
     * </p>
     * 
     * @param <T> 返回值的类型
     * @param key 缓存键
     * @param clazz 目标类型
     * @return 缓存的对象，如果不存在返回 null
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     * <p>
     * 从缓存中删除指定的 key
     * </p>
     * 
     * @param key 缓存键
     */
    void delete(String key);

    /**
     * 批量删除缓存
     * <p>
     * 从缓存中删除多个 key
     * </p>
     * 
     * @param keys 缓存键列表
     */
    void deleteBatch(List<String> keys);

    /**
     * 判断 key 是否存在
     * 
     * @param key 缓存键
     * @return true 表示 key 存在，false 表示不存在
     */
    boolean exists(String key);

    /**
     * 设置过期时间
     * <p>
     * 为已存在的 key 设置过期时间
     * </p>
     * 
     * @param key 缓存键
     * @param timeout 过期时间数值
     * @param unit 过期时间单位
     * @return true 表示设置成功，false 表示 key 不存在或设置失败
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取所有匹配的 key
     * <p>
     * 支持通配符匹配，如 "user:*" 匹配所有以 "user:" 开头的 key
     * </p>
     * <p>
     * 注意：在生产环境中，如果 key 数量很大，此操作可能很慢，建议谨慎使用
     * </p>
     * 
     * @param pattern 匹配模式，支持通配符 * 和 ?
     * @return 匹配的 key 集合
     */
    Set<String> keys(String pattern);

    /**
     * 递增
     * <p>
     * 将 key 对应的数值加 1，如果 key 不存在，则先初始化为 0 再加 1
     * </p>
     * 
     * @param key 缓存键
     * @return 递增后的值
     */
    Long increment(String key);

    /**
     * 递减
     * <p>
     * 将 key 对应的数值减 1，如果 key 不存在，则先初始化为 0 再减 1
     * </p>
     * 
     * @param key 缓存键
     * @return 递减后的值
     */
    Long decrement(String key);
}

