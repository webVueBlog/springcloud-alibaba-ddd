package com.example.transaction;

/**
 * 分布式事务服务接口
 * <p>
 * 基于 Seata 实现的分布式事务管理服务，提供编程式的事务管理方式
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>跨多个服务的事务操作</li>
 *   <li>需要编程式控制事务的场景</li>
 *   <li>需要获取事务 XID 的场景</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 方式1：执行带返回值的分布式事务
 * Order order = distributedTransactionService.execute(() -> {
 *     Order order = orderRepository.save(new Order(orderDTO));
 *     userService.updateUser(order.getUserId());
 *     return order;
 * });
 * 
 * // 方式2：执行无返回值的分布式事务
 * distributedTransactionService.executeWithoutResult(() -> {
 *     orderRepository.save(order);
 *     userService.updateUser(order.getUserId());
 * });
 * 
 * // 方式3：获取当前事务 XID
 * String xid = distributedTransactionService.getCurrentXid();
 * 
 * // 方式4：判断是否存在全局事务
 * if (distributedTransactionService.isGlobalTransaction()) {
 *     // 存在全局事务
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DistributedTransactionService {
    
    /**
     * 执行分布式事务（带返回值）
     * <p>
     * 在分布式事务中执行业务逻辑，如果发生异常会自动回滚所有操作
     * </p>
     * 
     * @param <T> 返回值类型
     * @param transactionCallback 事务回调，包含需要在事务中执行的业务逻辑
     * @return 业务逻辑的返回值
     * @throws Exception 如果业务逻辑执行异常，会触发事务回滚
     */
    <T> T execute(TransactionCallback<T> transactionCallback) throws Exception;
    
    /**
     * 执行分布式事务（无返回值）
     * <p>
     * 在分布式事务中执行业务逻辑，如果发生异常会自动回滚所有操作
     * </p>
     * 
     * @param transactionCallback 事务回调，包含需要在事务中执行的业务逻辑
     * @throws Exception 如果业务逻辑执行异常，会触发事务回滚
     */
    void executeWithoutResult(TransactionCallbackWithoutResult transactionCallback) throws Exception;
    
    /**
     * 获取当前事务 XID
     * <p>
     * XID 是全局事务的唯一标识，用于在分布式环境中追踪事务
     * </p>
     * 
     * @return 当前事务的 XID，如果不存在全局事务则返回 null
     */
    String getCurrentXid();
    
    /**
     * 判断当前是否存在全局事务
     * 
     * @return true 表示存在全局事务，false 表示不存在
     */
    boolean isGlobalTransaction();
}

