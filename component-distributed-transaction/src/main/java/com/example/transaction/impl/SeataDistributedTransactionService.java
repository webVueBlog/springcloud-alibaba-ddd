package com.example.transaction.impl;

import com.example.transaction.DistributedTransactionService;
import com.example.transaction.TransactionCallback;
import com.example.transaction.TransactionCallbackWithoutResult;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Seata 分布式事务服务实现
 * <p>
 * 基于 Seata 的 AT 模式实现分布式事务管理
 * 使用 {@link GlobalTransactional} 注解开启全局事务
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>自动回滚：发生异常时自动回滚所有操作</li>
 *   <li>支持跨服务：支持跨多个微服务的事务操作</li>
 *   <li>数据源代理：自动代理数据源，无需手动配置</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class SeataDistributedTransactionService implements DistributedTransactionService {

    /**
     * 执行分布式事务（带返回值）
     * <p>
     * 使用 {@link GlobalTransactional} 注解开启全局事务
     * 如果发生任何异常，会自动回滚所有操作
     * </p>
     * 
     * @param <T> 返回值类型
     * @param transactionCallback 事务回调
     * @return 业务逻辑的返回值
     * @throws Exception 如果业务逻辑执行异常，会触发事务回滚
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public <T> T execute(TransactionCallback<T> transactionCallback) throws Exception {
        log.debug("开始执行分布式事务（带返回值）");
        try {
            T result = transactionCallback.doInTransaction();
            log.debug("分布式事务执行成功");
            return result;
        } catch (Exception e) {
            log.error("分布式事务执行失败，将自动回滚", e);
            throw e;
        }
    }

    /**
     * 执行分布式事务（无返回值）
     * <p>
     * 使用 {@link GlobalTransactional} 注解开启全局事务
     * 如果发生任何异常，会自动回滚所有操作
     * </p>
     * 
     * @param transactionCallback 事务回调
     * @throws Exception 如果业务逻辑执行异常，会触发事务回滚
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void executeWithoutResult(TransactionCallbackWithoutResult transactionCallback) throws Exception {
        log.debug("开始执行分布式事务（无返回值）");
        try {
            transactionCallback.doInTransaction();
            log.debug("分布式事务执行成功");
        } catch (Exception e) {
            log.error("分布式事务执行失败，将自动回滚", e);
            throw e;
        }
    }

    /**
     * 获取当前事务 XID
     * <p>
     * 从 Seata 的全局事务上下文中获取当前事务的 XID
     * </p>
     * 
     * @return 当前事务的 XID，如果不存在全局事务则返回 null
     */
    @Override
    public String getCurrentXid() {
        try {
            String xid = GlobalTransactionContext.getCurrentOrCreate().getXid();
            log.debug("获取当前事务 XID: {}", xid);
            return xid;
        } catch (Exception e) {
            log.debug("获取当前事务 XID 失败，可能不存在全局事务", e);
            return null;
        }
    }

    /**
     * 判断当前是否存在全局事务
     * 
     * @return true 表示存在全局事务，false 表示不存在
     */
    @Override
    public boolean isGlobalTransaction() {
        try {
            String xid = GlobalTransactionContext.getCurrentOrCreate().getXid();
            boolean exists = xid != null && !xid.isEmpty();
            log.debug("判断是否存在全局事务: {}", exists);
            return exists;
        } catch (Exception e) {
            log.debug("判断是否存在全局事务失败", e);
            return false;
        }
    }
}

