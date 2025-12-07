package com.example.seckill.application.service;

import com.example.cache.CacheService;
import com.example.common.exception.BusinessException;
import com.example.lock.annotation.DistributedLockAnnotation;
import com.example.ratelimit.annotation.RateLimit;
import com.example.seckill.application.dto.SeckillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀应用服务
 * <p>
 * 负责协调领域对象完成秒杀业务用例，是应用层的核心
 * </p>
 * <p>
 * 职责说明：
 * <ul>
 *   <li>接收秒杀请求，执行秒杀逻辑</li>
 *   <li>使用分布式锁防止超卖</li>
 *   <li>使用限流保护系统</li>
 *   <li>管理秒杀库存（使用 Redis 缓存）</li>
 *   <li>防止用户重复参与秒杀</li>
 * </ul>
 * </p>
 * <p>
 * 技术特性：
 * <ul>
 *   <li>分布式锁：使用 @DistributedLockAnnotation 防止并发超卖</li>
 *   <li>限流保护：使用 @RateLimit 限制请求频率</li>
 *   <li>Redis 缓存：使用 Redis 管理秒杀库存，提高性能</li>
 *   <li>原子操作：使用 Redis 的 decrement 操作保证库存扣减的原子性</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class SeckillService {

    /** 缓存服务（必需，用于库存管理和订单记录） */
    @Autowired(required = false)
    private CacheService cacheService;

    /**
     * 秒杀下单
     * <p>
     * 执行秒杀的完整流程：
     * <ol>
     *   <li>使用分布式锁防止并发超卖</li>
     *   <li>使用限流保护系统</li>
     *   <li>检查用户是否已参与过该活动</li>
     *   <li>检查库存是否充足</li>
     *   <li>扣减库存（原子操作）</li>
     *   <li>生成订单号</li>
     *   <li>记录秒杀订单</li>
     * </ol>
     * </p>
     * <p>
     * 保护机制：
     * <ul>
     *   <li>分布式锁：防止同一活动的并发请求导致超卖</li>
     *   <li>限流：限制每秒请求数量，保护系统</li>
     *   <li>防重复：检查用户是否已参与，防止重复下单</li>
     * </ul>
     * </p>
     * 
     * @param activityId 活动ID，不能为 null
     * @param userId 用户ID，不能为 null
     * @return 秒杀结果，包含成功/失败状态、消息、剩余库存、订单号等信息
     */
    @DistributedLockAnnotation(key = "seckill:#{#activityId}", waitTime = 5, leaseTime = 10)
    @RateLimit(key = "seckill:#{#activityId}", limit = 10, window = 60)
    public SeckillResult seckill(Long activityId, Long userId) {
        Assert.notNull(activityId, "活动ID不能为空");
        Assert.notNull(userId, "用户ID不能为空");
        
        // 检查缓存服务是否配置
        if (cacheService == null) {
            log.error("CacheService未配置，无法执行秒杀操作。请检查Redis配置。");
            return SeckillResult.fail("系统配置错误，请联系管理员");
        }
        
        try {
            log.info("开始秒杀: activityId={}, userId={}", activityId, userId);
            
            // 检查用户是否已经参与过该活动
            String userOrderKey = "seckill:order:" + activityId + ":" + userId;
            if (cacheService.exists(userOrderKey)) {
                log.warn("用户已参与过该秒杀活动: activityId={}, userId={}", activityId, userId);
                return SeckillResult.fail("您已经参与过该秒杀活动");
            }
            
            // 检查库存
            String stockKey = "seckill:stock:" + activityId;
            Integer stock = cacheService.get(stockKey, Integer.class);
            
            if (stock == null || stock <= 0) {
                log.warn("秒杀活动库存不足: activityId={}, stock={}", activityId, stock);
                return SeckillResult.fail("库存不足");
            }

            // 扣减库存（原子操作）
            Long newStock = cacheService.decrement(stockKey);
            if (newStock < 0) {
                // 库存不足，回滚
                cacheService.increment(stockKey);
                log.warn("秒杀活动库存不足（扣减后）: activityId={}, userId={}", activityId, userId);
                return SeckillResult.fail("库存不足");
            }

            // 生成订单号
            String orderNo = generateOrderNo(activityId, userId);
            
            // 记录秒杀订单（1小时过期，防止重复参与）
            cacheService.set(userOrderKey, orderNo, 1, TimeUnit.HOURS);
            
            // 记录订单详情（24小时过期，用于后续处理）
            String orderDetailKey = "seckill:order:detail:" + orderNo;
            cacheService.set(orderDetailKey, 
                String.format("activityId:%d,userId:%d,orderNo:%s", activityId, userId, orderNo),
                24, TimeUnit.HOURS);

            log.info("秒杀成功: activityId={}, userId={}, orderNo={}, remainingStock={}", 
                activityId, userId, orderNo, newStock);
            
            return SeckillResult.success(newStock, orderNo);
        } catch (Exception e) {
            log.error("秒杀异常: activityId={}, userId={}", activityId, userId, e);
            return SeckillResult.fail("秒杀失败: " + e.getMessage());
        }
    }

    /**
     * 初始化秒杀活动库存
     * <p>
     * 在秒杀活动开始前，将库存数量设置到 Redis 缓存中
     * </p>
     * 
     * @param activityId 活动ID，不能为 null
     * @param stock 库存数量，必须大于 0
     * @throws IllegalStateException 如果缓存服务未配置
     * @throws IllegalArgumentException 如果库存数量无效
     */
    public void initStock(Long activityId, Integer stock) {
        Assert.notNull(activityId, "活动ID不能为空");
        Assert.notNull(stock, "库存数量不能为空");
        Assert.isTrue(stock > 0, "库存数量必须大于0");
        
        if (cacheService == null) {
            throw new IllegalStateException("CacheService未配置，无法初始化库存。请检查Redis配置。");
        }
        
        try {
            String stockKey = "seckill:stock:" + activityId;
            // 设置库存，24小时过期
            cacheService.set(stockKey, stock, 24, TimeUnit.HOURS);
            log.info("初始化秒杀活动库存成功: activityId={}, stock={}", activityId, stock);
        } catch (Exception e) {
            log.error("初始化秒杀活动库存失败: activityId={}, stock={}", activityId, stock, e);
            throw new BusinessException("初始化库存失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取剩余库存
     * <p>
     * 从 Redis 缓存中获取秒杀活动的剩余库存
     * </p>
     * 
     * @param activityId 活动ID，不能为 null
     * @return 剩余库存数量，如果缓存服务未配置返回 0
     */
    public Long getRemainingStock(Long activityId) {
        Assert.notNull(activityId, "活动ID不能为空");
        
        if (cacheService == null) {
            log.warn("CacheService未配置，返回库存为0");
            return 0L;
        }
        
        try {
            String stockKey = "seckill:stock:" + activityId;
            Integer stock = cacheService.get(stockKey, Integer.class);
            Long remainingStock = stock != null ? stock.longValue() : 0L;
            log.debug("获取剩余库存: activityId={}, remainingStock={}", activityId, remainingStock);
            return remainingStock;
        } catch (Exception e) {
            log.error("获取剩余库存失败: activityId={}", activityId, e);
            return 0L;
        }
    }
    
    /**
     * 检查用户是否已参与
     * <p>
     * 检查指定用户是否已经参与过指定秒杀活动
     * </p>
     * 
     * @param activityId 活动ID，不能为 null
     * @param userId 用户ID，不能为 null
     * @return true 表示已参与，false 表示未参与
     */
    public boolean hasParticipated(Long activityId, Long userId) {
        Assert.notNull(activityId, "活动ID不能为空");
        Assert.notNull(userId, "用户ID不能为空");
        
        if (cacheService == null) {
            log.warn("CacheService未配置，返回未参与");
            return false;
        }
        
        try {
            String userOrderKey = "seckill:order:" + activityId + ":" + userId;
            boolean participated = cacheService.exists(userOrderKey);
            log.debug("检查用户是否已参与: activityId={}, userId={}, participated={}", 
                    activityId, userId, participated);
            return participated;
        } catch (Exception e) {
            log.error("检查用户是否已参与失败: activityId={}, userId={}", activityId, userId, e);
            return false;
        }
    }
    
    /**
     * 生成订单号
     * <p>
     * 订单号格式：SK + 活动ID + 用户ID + 时间戳 + 8位随机字符串（大写）
     * 示例：SK110012345678901234567890ABCDEFGH
     * </p>
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 订单号
     */
    private String generateOrderNo(Long activityId, Long userId) {
        String orderNo = "SK" + activityId + userId + System.currentTimeMillis() + 
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.debug("生成订单号: activityId={}, userId={}, orderNo={}", activityId, userId, orderNo);
        return orderNo;
    }
}

