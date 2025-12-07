package com.example.ratelimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * <p>
 * 用于方法级别的限流控制，基于滑动窗口算法实现
 * 当方法被调用时，会自动进行限流检查，如果超过限制则抛出异常
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 基本使用：限制每个IP每分钟最多100次请求
 * @RateLimit(limit = 100, window = 60)
 * public Result&lt;String&gt; login(LoginDTO loginDTO) {
 *     // 登录逻辑
 * }
 * 
 * // 指定限流key：限制每个用户每分钟最多10次请求
 * @RateLimit(key = "user:#{#loginDTO.userId}", limit = 10, window = 60)
 * public Result&lt;String&gt; login(LoginDTO loginDTO) {
 *     // 登录逻辑
 * }
 * 
 * // 限制每个IP每小时最多1000次请求
 * @RateLimit(limit = 1000, window = 3600)
 * public Result&lt;String&gt; api() {
 *     // API逻辑
 * }
 * </pre>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>如果 key 为空，会自动使用请求 URI + IP 地址作为 key</li>
 *   <li>如果不在 Web 环境中，会使用方法签名作为 key</li>
 *   <li>限流失败会抛出 BusinessException，状态码为 429</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 限流键
     * <p>
     * 用于区分不同的限流对象，支持 SpEL 表达式
     * 如果为空，会自动生成：
     * <ul>
     *   <li>Web 环境：使用请求 URI + IP 地址</li>
     *   <li>非 Web 环境：使用方法签名</li>
     * </ul>
     * </p>
     * <p>
     * 示例：
     * <ul>
     *   <li>空字符串：自动生成</li>
     *   <li>"user:#{#userId}"：使用用户ID作为key</li>
     *   <li>"api:login"：固定key</li>
     * </ul>
     * </p>
     * 
     * @return 限流键，默认为空字符串（自动生成）
     */
    String key() default "";

    /**
     * 限制次数
     * <p>
     * 在时间窗口内允许的最大请求数
     * </p>
     * 
     * @return 限制次数，默认 100
     */
    int limit() default 100;

    /**
     * 时间窗口（秒）
     * <p>
     * 限流的时间窗口大小，在此时间窗口内最多允许 limit 次请求
     * </p>
     * 
     * @return 时间窗口大小（秒），默认 60
     */
    int window() default 60;
}

