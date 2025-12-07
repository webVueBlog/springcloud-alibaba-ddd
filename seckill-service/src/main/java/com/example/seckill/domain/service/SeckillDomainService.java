package com.example.seckill.domain.service;

import com.example.seckill.domain.model.SeckillActivity;

/**
 * 秒杀领域服务接口
 * <p>
 * 定义秒杀活动的业务规则和业务逻辑，属于领域层
 * </p>
 * <p>
 * 职责说明：
 * <ul>
 *   <li>活动有效性检查：检查活动状态、时间等</li>
 *   <li>库存管理：检查库存、扣减库存等</li>
 *   <li>业务规则验证：验证业务规则是否满足</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在应用服务中调用领域服务验证业务规则</li>
 *   <li>保持领域层的业务逻辑纯净</li>
 *   <li>便于业务规则的复用和测试</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SeckillDomainService {
    
    /**
     * 检查活动是否有效
     * <p>
     * 验证活动的有效性，包括：
     * <ul>
     *   <li>活动对象不为 null</li>
     *   <li>活动状态为 1（进行中）</li>
     *   <li>当前时间在活动时间范围内</li>
     * </ul>
     * </p>
     * 
     * @param activity 秒杀活动，不能为 null
     * @return true 表示活动有效，false 表示活动无效
     */
    boolean isActivityValid(SeckillActivity activity);
    
    /**
     * 检查活动是否在进行中
     * <p>
     * 验证活动是否正在进行，包括：
     * <ul>
     *   <li>活动有效性检查</li>
     *   <li>当前时间在活动时间范围内</li>
     * </ul>
     * </p>
     * 
     * @param activity 秒杀活动，不能为 null
     * @return true 表示活动正在进行，false 表示活动未进行
     */
    boolean isActivityActive(SeckillActivity activity);
    
    /**
     * 检查库存是否充足
     * <p>
     * 验证活动库存是否充足，即剩余库存是否大于 0
     * 剩余库存 = stock - sold
     * </p>
     * 
     * @param activity 秒杀活动，不能为 null
     * @return true 表示库存充足，false 表示库存不足
     */
    boolean hasStock(SeckillActivity activity);
    
    /**
     * 扣减库存
     * <p>
     * 扣减活动库存，将已售数量加 1
     * 注意：此方法只修改领域模型，不进行持久化
     * </p>
     * 
     * @param activity 秒杀活动，不能为 null
     * @return 扣减后的剩余库存，如果库存不足返回 null
     */
    Integer deductStock(SeckillActivity activity);
}

