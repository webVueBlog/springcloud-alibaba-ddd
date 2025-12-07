package com.example.transaction;

/**
 * 无返回值的分布式事务回调接口
 * <p>
 * 用于在分布式事务中执行业务逻辑的函数式接口（无返回值）
 * 配合 {@link DistributedTransactionService#executeWithoutResult} 使用
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * distributedTransactionService.executeWithoutResult(() -> {
 *     // 在事务中执行的业务逻辑
 *     orderRepository.save(order);
 *     userService.updateUser(order.getUserId());
 * });
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface TransactionCallbackWithoutResult {
    
    /**
     * 在分布式事务中执行业务逻辑
     * <p>
     * 此方法中的代码会在分布式事务中执行，如果发生异常会自动回滚
     * </p>
     * 
     * @throws Exception 业务逻辑可能抛出的异常，异常会导致事务回滚
     */
    void doInTransaction() throws Exception;
}

