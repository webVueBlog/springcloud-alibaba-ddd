package com.example.lock;

/**
 * 锁回调接口
 * <p>
 * 用于在获取到分布式锁后执行业务逻辑的函数式接口
 * 配合 {@link DistributedLock#executeWithLock} 使用
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * distributedLock.executeWithLock("lock:key", 5, 10, TimeUnit.SECONDS, () -> {
 *     // 执行业务逻辑
 *     return result;
 * });
 * </pre>
 * </p>
 * 
 * @param <T> 返回值类型
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface LockCallback<T> {
    
    /**
     * 执行业务逻辑
     * <p>
     * 在获取到分布式锁后执行此方法
     * </p>
     * 
     * @return 业务逻辑的返回值
     * @throws Exception 业务逻辑可能抛出的异常
     */
    T execute() throws Exception;
}

