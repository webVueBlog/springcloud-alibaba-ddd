package com.example.transaction;

/**
 * 分布式事务回调接口
 * <p>
 * 用于在分布式事务中执行业务逻辑的函数式接口
 * 配合 {@link DistributedTransactionService#execute} 使用
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * Order order = distributedTransactionService.execute(() -> {
 *     // 在事务中执行的业务逻辑
 *     Order order = orderRepository.save(new Order(orderDTO));
 *     userService.updateUser(order.getUserId());
 *     return order;
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
public interface TransactionCallback<T> {
    
    /**
     * 在分布式事务中执行业务逻辑
     * <p>
     * 此方法中的代码会在分布式事务中执行，如果发生异常会自动回滚
     * </p>
     * 
     * @return 业务逻辑的返回值
     * @throws Exception 业务逻辑可能抛出的异常，异常会导致事务回滚
     */
    T doInTransaction() throws Exception;
}

