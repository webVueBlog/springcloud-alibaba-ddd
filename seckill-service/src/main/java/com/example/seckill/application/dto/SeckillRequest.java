package com.example.seckill.application.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 秒杀请求 DTO
 * <p>
 * 用于接收秒杀下单的请求参数
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在 Controller 中接收 HTTP 请求参数</li>
 *   <li>在应用服务中进行秒杀操作</li>
 *   <li>使用 Bean Validation 进行参数校验</li>
 * </ul>
 * </p>
 * <p>
 * 验证规则：
 * <ul>
 *   <li>activityId 不能为空，必须大于 0</li>
 *   <li>userId 不能为空，必须大于 0</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class SeckillRequest {
    
    /**
     * 活动ID
     * <p>
     * 参与秒杀的活动ID，不能为空，必须大于 0
     * </p>
     */
    @NotNull(message = "活动ID不能为空")
    @Positive(message = "活动ID必须大于0")
    private Long activityId;
    
    /**
     * 用户ID
     * <p>
     * 参与秒杀的用户ID，不能为空，必须大于 0
     * </p>
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须大于0")
    private Long userId;
}

