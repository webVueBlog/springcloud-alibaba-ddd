package com.example.seckill.application.dto;

import lombok.Data;

/**
 * 秒杀结果 DTO
 * <p>
 * 用于返回秒杀操作的结果信息
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在应用服务中返回秒杀操作结果</li>
 *   <li>在 Controller 中返回给客户端</li>
 *   <li>包含成功/失败状态、消息、剩余库存、订单号等信息</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 成功结果
 * SeckillResult result = SeckillResult.success(remainingStock, orderNo);
 * 
 * // 失败结果
 * SeckillResult result = SeckillResult.fail("库存不足");
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class SeckillResult {
    
    /**
     * 是否成功
     * <p>
     * true 表示秒杀成功，false 表示秒杀失败
     * </p>
     */
    private Boolean success;
    
    /**
     * 消息
     * <p>
     * 秒杀操作的结果消息，如 "秒杀成功"、"库存不足"、"您已经参与过该秒杀活动" 等
     * </p>
     */
    private String message;
    
    /**
     * 剩余库存
     * <p>
     * 秒杀操作后的剩余库存数量，仅在成功时返回
     * </p>
     */
    private Long remainingStock;
    
    /**
     * 订单号
     * <p>
     * 秒杀成功时生成的订单号，仅在成功时返回
     * </p>
     */
    private String orderNo;
    
    /**
     * 创建成功结果
     * 
     * @param remainingStock 剩余库存
     * @param orderNo 订单号
     * @return 成功结果对象
     */
    public static SeckillResult success(Long remainingStock, String orderNo) {
        SeckillResult result = new SeckillResult();
        result.setSuccess(true);
        result.setMessage("秒杀成功");
        result.setRemainingStock(remainingStock);
        result.setOrderNo(orderNo);
        return result;
    }
    
    /**
     * 创建失败结果
     * 
     * @param message 失败消息
     * @return 失败结果对象
     */
    public static SeckillResult fail(String message) {
        SeckillResult result = new SeckillResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}

