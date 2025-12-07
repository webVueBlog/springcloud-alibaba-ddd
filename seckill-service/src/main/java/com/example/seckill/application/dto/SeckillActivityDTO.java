package com.example.seckill.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 秒杀活动 DTO
 * <p>
 * 用于传输秒杀活动信息，包含活动基本信息和计算属性
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在 Controller 中返回秒杀活动信息</li>
 *   <li>在应用服务中传输活动数据</li>
 *   <li>包含计算属性（剩余库存、是否进行中等）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class SeckillActivityDTO {
    
    /**
     * 活动ID
     */
    private Long id;
    
    /**
     * 活动名称
     */
    private String activityName;
    
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 活动库存数量
     */
    private Integer stock;
    
    /**
     * 已售数量
     */
    private Integer sold;
    
    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 活动状态
     * <p>
     * 0-未开始，1-进行中，2-已结束，3-已取消
     * </p>
     */
    private Integer status;
    
    /**
     * 剩余库存
     * <p>
     * 计算属性：剩余库存 = stock - sold
     * </p>
     */
    private Integer remainingStock;
    
    /**
     * 是否进行中
     * <p>
     * 计算属性：根据活动状态和时间判断活动是否正在进行
     * </p>
     */
    private Boolean isActive;
}

