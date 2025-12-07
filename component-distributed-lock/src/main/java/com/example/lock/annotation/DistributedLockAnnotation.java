package com.example.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * <p>
 * 在方法上使用此注解，可以自动为方法添加分布式锁
 * 锁的 key 支持 SpEL 表达式，可以从方法参数中获取值
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 基本使用
 * @DistributedLockAnnotation(key = "lock:user:1")
 * public void updateUser(Long id) {
 *     // 业务逻辑
 * }
 * 
 * // 使用 SpEL 表达式从参数中获取值
 * @DistributedLockAnnotation(key = "lock:user:#{#id}", waitTime = 5, leaseTime = 10)
 * public void updateUser(Long id) {
 *     // 业务逻辑
 * }
 * 
 * // 多个参数
 * @DistributedLockAnnotation(key = "lock:order:#{#orderId}:user:#{#userId}")
 * public void processOrder(Long orderId, Long userId) {
 *     // 业务逻辑
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLockAnnotation {
    
    /**
     * 锁的 key
     * <p>
     * 支持 SpEL 表达式，可以从方法参数中获取值
     * 例如：key = "lock:user:#{#id}" 会从方法参数 id 中获取值
     * </p>
     * 
     * @return 锁的 key
     */
    String key();

    /**
     * 等待时间（秒）
     * <p>
     * 如果锁已被其他线程持有，最多等待这么长时间
     * 默认 5 秒
     * </p>
     * 
     * @return 等待时间（秒）
     */
    long waitTime() default 5;

    /**
     * 锁过期时间（秒）
     * <p>
     * 获取锁后，锁会在这么长时间后自动释放，防止死锁
     * 默认 10 秒
     * </p>
     * 
     * @return 锁过期时间（秒）
     */
    long leaseTime() default 10;
}

