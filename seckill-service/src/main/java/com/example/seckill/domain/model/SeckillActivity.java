package com.example.seckill.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 秒杀活动领域模型
 * <p>
 * 秒杀活动的聚合根，包含秒杀活动的核心业务属性
 * </p>
 * <p>
 * 活动状态说明：
 * <ul>
 *   <li>0 - 未开始：活动尚未开始</li>
 *   <li>1 - 进行中：活动正在进行</li>
 *   <li>2 - 已结束：活动已结束</li>
 *   <li>3 - 已取消：活动已取消</li>
 * </ul>
 * </p>
 * <p>
 * 库存说明：
 * <ul>
 *   <li>stock：活动总库存数量</li>
 *   <li>sold：已售数量</li>
 *   <li>剩余库存 = stock - sold</li>
 * </ul>
 * </p>
 * <p>
 * 继承说明：
 * <ul>
 *   <li>继承 BaseEntity，包含 id、createTime、updateTime、deleted 等基础字段</li>
 *   <li>使用 MyBatis Plus 的逻辑删除功能</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivity extends BaseEntity {
    
    /**
     * 活动名称
     * <p>
     * 用于标识活动的具体名称，如 "双11秒杀活动"、"春节特惠秒杀" 等
     * </p>
     */
    private String activityName;
    
    /**
     * 产品ID
     * <p>
     * 参与秒杀活动的产品ID
     * </p>
     */
    private Long productId;
    
    /**
     * 活动库存数量
     * <p>
     * 该活动可供参与的总数量，必须大于 0
     * </p>
     */
    private Integer stock;
    
    /**
     * 已售数量
     * <p>
     * 该活动已被参与的数量，初始值为 0
     * </p>
     */
    private Integer sold;
    
    /**
     * 活动开始时间
     * <p>
     * 活动开始的具体时间点，在此时间之前活动未开始
     * </p>
     */
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     * <p>
     * 活动结束的具体时间点，在此时间之后活动已结束
     * </p>
     */
    private LocalDateTime endTime;
    
    /**
     * 活动状态
     * <p>
     * 0-未开始，1-进行中，2-已结束，3-已取消
     * </p>
     */
    private Integer status;
}

