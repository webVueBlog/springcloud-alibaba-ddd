package com.example.lock.impl;

import com.example.lock.DistributedLock;
import com.example.lock.LockCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redisson 的分布式锁实现
 * <p>
 * 使用 Redisson 提供的分布式锁功能，支持可重入锁、公平锁等特性
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>可重入锁：同一线程可以多次获取同一把锁</li>
 *   <li>自动续期：如果业务执行时间超过锁的过期时间，会自动续期</li>
 *   <li>防止死锁：锁有过期时间，即使业务异常也不会导致死锁</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonDistributedLock implements DistributedLock {

    /** Redisson 客户端 */
    private final RedissonClient redissonClient;

    /**
     * 尝试加锁（立即返回）
     * 
     * @param key 锁的键
     * @return true 表示获取锁成功，false 表示获取锁失败
     */
    @Override
    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        boolean acquired = lock.tryLock();
        log.debug("尝试加锁: key={}, result={}", key, acquired);
        return acquired;
    }

    /**
     * 尝试加锁（带等待时间）
     * 
     * @param key 锁的键
     * @param waitTime 等待时间
     * @param unit 时间单位
     * @return true 表示获取锁成功，false 表示在等待时间内未获取到锁
     */
    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean acquired = lock.tryLock(waitTime, unit);
            log.debug("尝试加锁: key={}, waitTime={} {}, result={}", key, waitTime, unit, acquired);
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: key={}", key, e);
            return false;
        }
    }

    /**
     * 尝试加锁（带等待时间和锁过期时间）
     * 
     * @param key 锁的键
     * @param waitTime 等待时间
     * @param leaseTime 锁的过期时间
     * @param unit 时间单位
     * @return true 表示获取锁成功，false 表示在等待时间内未获取到锁
     */
    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, unit);
            log.debug("尝试加锁: key={}, waitTime={} {}, leaseTime={} {}, result={}", 
                    key, waitTime, unit, leaseTime, unit, acquired);
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: key={}", key, e);
            return false;
        }
    }

    /**
     * 释放锁
     * <p>
     * 只有持有锁的当前线程才能释放锁
     * </p>
     * 
     * @param key 锁的键
     */
    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("释放锁成功: key={}", key);
        } else {
            log.warn("当前线程未持有锁，无法释放: key={}", key);
        }
    }

    /**
     * 执行带锁的业务逻辑
     * <p>
     * 自动获取锁、执行业务逻辑、释放锁，确保锁一定会被释放
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
    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockCallback<T> callback) {
        RLock lock = redissonClient.getLock(key);
        try {
            log.debug("尝试获取锁: key={}, waitTime={} {}, leaseTime={} {}", key, waitTime, unit, leaseTime, unit);
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                try {
                    log.debug("获取锁成功，执行业务逻辑: key={}", key);
                    return callback.execute();
                } catch (Exception e) {
                    log.error("业务逻辑执行异常: key={}", key, e);
                    throw new RuntimeException("业务逻辑执行异常: " + key, e);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.debug("释放锁成功: key={}", key);
                    }
                }
            } else {
                log.warn("获取锁失败: key={}, waitTime={} {}", key, waitTime, unit);
                throw new RuntimeException("获取锁失败: " + key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: key={}", key, e);
            throw new RuntimeException("获取锁被中断: " + key, e);
        }
    }
}

