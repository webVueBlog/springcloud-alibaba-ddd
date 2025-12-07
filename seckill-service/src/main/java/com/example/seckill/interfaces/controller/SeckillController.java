package com.example.seckill.interfaces.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.seckill.application.dto.SeckillResult;
import com.example.seckill.application.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 秒杀控制器
 * <p>
 * 提供秒杀相关的 REST API 接口，属于接口层（interfaces）
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>秒杀下单：执行秒杀操作</li>
 *   <li>初始化库存：在活动开始前初始化库存</li>
 *   <li>查询库存：查询活动的剩余库存</li>
 *   <li>检查参与：检查用户是否已参与活动</li>
 * </ul>
 * </p>
 * <p>
 * 请求路径：/api/seckill
 * </p>
 * <p>
 * 参数验证：
 * <ul>
 *   <li>使用 @Validated 验证路径参数和请求参数</li>
 *   <li>使用 Bean Validation 注解进行参数校验</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/seckill")
@RequiredArgsConstructor
@Validated
public class SeckillController {

    /** 秒杀应用服务 */
    private final SeckillService seckillService;

    /**
     * 秒杀下单
     * <p>
     * 执行秒杀操作，包含分布式锁和限流保护
     * </p>
     * 
     * @param activityId 活动ID，必须大于 0
     * @param userId 用户ID，必须大于 0
     * @return 秒杀结果
     */
    @PostMapping("/{activityId}")
    public Result<SeckillResult> seckill(
            @PathVariable @NotNull(message = "活动ID不能为空") @Positive(message = "活动ID必须大于0") Long activityId,
            @RequestParam @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId) {
        try {
            log.info("收到秒杀请求: activityId={}, userId={}", activityId, userId);
            
            SeckillResult result = seckillService.seckill(activityId, userId);
            
            if (result.getSuccess()) {
                log.info("秒杀成功: activityId={}, userId={}, orderNo={}", 
                        activityId, userId, result.getOrderNo());
                return Result.success(result);
            } else {
                log.warn("秒杀失败: activityId={}, userId={}, message={}", 
                        activityId, userId, result.getMessage());
                return Result.error(result.getMessage());
            }
        } catch (BusinessException e) {
            log.warn("秒杀业务异常: activityId={}, userId={}, message={}", 
                    activityId, userId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("秒杀异常: activityId={}, userId={}", activityId, userId, e);
            return Result.error("秒杀失败: " + e.getMessage());
        }
    }

    /**
     * 初始化秒杀活动库存
     * <p>
     * 在秒杀活动开始前，将库存数量设置到 Redis 缓存中
     * </p>
     * 
     * @param activityId 活动ID，必须大于 0
     * @param stock 库存数量，必须大于 0
     * @return 操作结果
     */
    @PostMapping("/init/{activityId}")
    public Result<Void> initStock(
            @PathVariable @NotNull(message = "活动ID不能为空") @Positive(message = "活动ID必须大于0") Long activityId,
            @RequestParam @NotNull(message = "库存数量不能为空") @Positive(message = "库存数量必须大于0") Integer stock) {
        try {
            log.info("初始化秒杀活动库存: activityId={}, stock={}", activityId, stock);
            
            seckillService.initStock(activityId, stock);
            
            log.info("初始化秒杀活动库存成功: activityId={}, stock={}", activityId, stock);
            return Result.success();
        } catch (BusinessException e) {
            log.warn("初始化库存业务异常: activityId={}, stock={}, message={}", 
                    activityId, stock, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("初始化库存异常: activityId={}, stock={}", activityId, stock, e);
            return Result.error("初始化库存失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取剩余库存
     * <p>
     * 查询指定秒杀活动的剩余库存数量
     * </p>
     * 
     * @param activityId 活动ID，必须大于 0
     * @return 剩余库存数量
     */
    @GetMapping("/stock/{activityId}")
    public Result<Long> getRemainingStock(
            @PathVariable @NotNull(message = "活动ID不能为空") @Positive(message = "活动ID必须大于0") Long activityId) {
        try {
            Long stock = seckillService.getRemainingStock(activityId);
            log.debug("查询剩余库存: activityId={}, stock={}", activityId, stock);
            return Result.success(stock);
        } catch (Exception e) {
            log.error("查询剩余库存异常: activityId={}", activityId, e);
            return Result.error("查询库存失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否已参与
     * <p>
     * 检查指定用户是否已经参与过指定秒杀活动
     * </p>
     * 
     * @param activityId 活动ID，必须大于 0
     * @param userId 用户ID，必须大于 0
     * @return true 表示已参与，false 表示未参与
     */
    @GetMapping("/check/{activityId}")
    public Result<Boolean> hasParticipated(
            @PathVariable @NotNull(message = "活动ID不能为空") @Positive(message = "活动ID必须大于0") Long activityId,
            @RequestParam @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId) {
        try {
            boolean participated = seckillService.hasParticipated(activityId, userId);
            log.debug("检查用户是否已参与: activityId={}, userId={}, participated={}", 
                    activityId, userId, participated);
            return Result.success(participated);
        } catch (Exception e) {
            log.error("检查用户是否已参与异常: activityId={}, userId={}", activityId, userId, e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }
}

