package com.example.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 * <p>
 * 提供分布式环境下的锁机制，确保同一时刻只有一个线程能够执行被锁保护的代码块
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>防止并发操作导致的数据不一致</li>
 *   <li>秒杀、抢购等场景的库存控制</li>
 *   <li>分布式任务调度，确保任务不重复执行</li>
 *   <li>分布式事务的协调</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 方式1：手动加锁和释放
 * if (distributedLock.tryLock("lock:key", 5, 10, TimeUnit.SECONDS)) {
 *     try {
 *         // 业务逻辑
 *     } finally {
 *         distributedLock.unlock("lock:key");
 *     }
 * }
 * 
 * // 方式2：使用回调方式（推荐）
 * distributedLock.executeWithLock("lock:key", 5, 10, TimeUnit.SECONDS, () -> {
 *     // 业务逻辑
 *     return result;
 * });
 * 
 * // 方式3：使用注解（推荐）
 * @DistributedLockAnnotation(key = "lock:#{#id}", waitTime = 5, leaseTime = 10)
 * public void doSomething(Long id) {
 *     // 业务逻辑
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DistributedLock {
    
    /**
     * 尝试加锁（立即返回）
     * <p>
     * 尝试获取锁，如果锁已被其他线程持有，立即返回 false，不等待
     * </p>
     * 
     * @param key 锁的键，建议使用命名空间格式，如 "lock:user:1"
     * @return true 表示获取锁成功，false 表示获取锁失败
     */
    boolean tryLock(String key);

    /**
     * 尝试加锁（带等待时间）
     * <p>
     * 尝试获取锁，如果锁已被其他线程持有，会等待指定的时间
     * 如果在等待时间内获取到锁，返回 true；否则返回 false
     * </p>
     * 
     * @param key 锁的键
     * @param waitTime 等待时间，如果锁被占用，最多等待这么长时间
     * @param unit 时间单位
     * @return true 表示获取锁成功，false 表示在等待时间内未获取到锁
     */
    boolean tryLock(String key, long waitTime, TimeUnit unit);

    /**
     * 尝试加锁（带等待时间和锁过期时间）
     * <p>
     * 尝试获取锁，如果锁已被其他线程持有，会等待指定的时间
     * 获取到锁后，锁会在 leaseTime 后自动释放（防止死锁）
     * </p>
     * 
     * @param key 锁的键
     * @param waitTime 等待时间，如果锁被占用，最多等待这么长时间
     * @param leaseTime 锁的过期时间，获取锁后，锁会在这么长时间后自动释放
     * @param unit 时间单位
     * @return true 表示获取锁成功，false 表示在等待时间内未获取到锁
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     * <p>
     * 释放指定 key 的锁，只有持有锁的线程才能释放锁
     * </p>
     * 
     * @param key 锁的键
     */
    void unlock(String key);

    /**
     * 执行带锁的业务逻辑（推荐使用）
     * <p>
     * 自动获取锁、执行业务逻辑、释放锁，确保锁一定会被释放
     * 如果获取锁失败，会抛出 RuntimeException
     * </p>
     * 
     * @param <T> 返回值类型
     * @param key 锁的键
     * @param waitTime 等待时间
     * @param leaseTime 锁的过期时间
     * @param unit 时间单位
     * @param callback 业务逻辑回调
     * @return 业务逻辑的返回值
     * @throws RuntimeException 如果获取锁失败或业务逻辑执行异常
     */
    <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockCallback<T> callback);
}

