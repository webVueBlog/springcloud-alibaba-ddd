package com.example.transaction.util;

import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 分布式事务工具类
 * <p>
 * 提供分布式事务相关的静态工具方法，包括：
 * <ul>
 *   <li>获取当前事务 XID</li>
 *   <li>判断是否存在全局事务</li>
 *   <li>手动回滚事务（不推荐使用）</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 获取当前事务 XID
 * String xid = TransactionUtil.getCurrentXid();
 * 
 * // 判断是否存在全局事务
 * if (TransactionUtil.hasGlobalTransaction()) {
 *     // 存在全局事务
 * }
 * 
 * // 手动回滚（通常不需要）
 * TransactionUtil.rollback();
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class TransactionUtil {
    
    /**
     * 私有构造函数，防止实例化
     */
    private TransactionUtil() {
        throw new UnsupportedOperationException("TransactionUtil class cannot be instantiated");
    }
    
    /**
     * 获取当前全局事务 XID
     * <p>
     * 优先从 RootContext 获取，如果不存在则从 GlobalTransactionContext 获取
     * </p>
     * 
     * @return 事务 XID，如果不存在则返回 null
     */
    public static String getCurrentXid() {
        try {
            // 优先从 RootContext 获取（线程本地变量）
            String xid = RootContext.getXID();
            if (xid != null && !xid.isEmpty()) {
                log.debug("从 RootContext 获取事务 XID: {}", xid);
                return xid;
            }
            
            // 如果 RootContext 中没有，则从 GlobalTransactionContext 获取
            xid = GlobalTransactionContext.getCurrentOrCreate().getXid();
            log.debug("从 GlobalTransactionContext 获取事务 XID: {}", xid);
            return xid;
        } catch (Exception e) {
            log.debug("获取当前事务 XID 失败，可能不存在全局事务", e);
            return null;
        }
    }
    
    /**
     * 判断当前是否存在全局事务
     * <p>
     * 优先从 RootContext 判断，如果不存在则从 GlobalTransactionContext 判断
     * </p>
     * 
     * @return true 表示存在全局事务，false 表示不存在
     */
    public static boolean hasGlobalTransaction() {
        try {
            // 优先从 RootContext 判断
            String xid = RootContext.getXID();
            if (xid != null && !xid.isEmpty()) {
                log.debug("从 RootContext 判断存在全局事务: {}", xid);
                return true;
            }
            
            // 如果 RootContext 中没有，则从 GlobalTransactionContext 判断
            xid = GlobalTransactionContext.getCurrentOrCreate().getXid();
            boolean exists = xid != null && !xid.isEmpty();
            log.debug("从 GlobalTransactionContext 判断是否存在全局事务: {}", exists);
            return exists;
        } catch (Exception e) {
            log.debug("判断是否存在全局事务失败", e);
            return false;
        }
    }
    
    /**
     * 手动回滚当前全局事务
     * <p>
     * <strong>注意：通常不需要手动调用此方法</strong>
     * Seata 会在发生异常时自动回滚事务
     * 只有在特殊场景下才需要手动回滚
     * </p>
     * 
     * @see io.seata.tm.api.GlobalTransaction#rollback()
     */
    public static void rollback() {
        try {
            String xid = RootContext.getXID();
            if (xid != null && !xid.isEmpty()) {
                log.warn("手动回滚全局事务: xid={}", xid);
                GlobalTransactionContext.reload(xid).rollback();
            } else {
                log.warn("尝试手动回滚事务，但当前不存在全局事务");
            }
        } catch (Exception e) {
            log.error("手动回滚事务失败", e);
            // 忽略异常，避免影响业务逻辑
        }
    }
}

